/*
 * Copyright (c) 2022 Eric Thiebaut-George.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { Component, OnInit, OnDestroy, ElementRef, ViewChild, HostListener } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import {HttpResponse} from "@angular/common/http";
import {Environment, IEnvironment} from "app/shared/model/environment.model";
import {EnvironmentGroup, IEnvironmentGroup} from "app/shared/model/environment-group.model";
import {EnvironmentGroupService} from "app/entities/environment-group/environment-group.service";
import {IProduct} from "app/shared/model/product.model";
import {ProductService} from "app/entities/product/product.service";
import {EnvironmentService} from "app/entities/environment/environment.service";
import {IDeployment} from "app/shared/model/deployment.model";
import {DeploymentService} from "app/entities/deployment/deployment.service";
import {Router, ActivatedRoute, NavigationEnd} from '@angular/router';
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";
import {Authority} from "app/shared/constants/authority.constants";
import {NgsgOrderChange} from "ng-sortgrid";
import {CdkDragDrop, moveItemInArray} from "@angular/cdk/drag-drop";
import { Clipboard } from '@angular/cdk/clipboard';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TranslateService} from "@ngx-translate/core";
import {FormBuilder, Validators} from "@angular/forms";
import * as moment from "moment";
import {DATE_TIME_FORMAT} from "app/shared/constants/input.constants";


@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;
  environmentGroups?: IEnvironmentGroup[];
  products?: IProduct[];
  productsForEnvGroups: Map<String, IProduct[]> = new Map();
  allEnvironments?: IEnvironment[] = [];
  environments: Map<String, IEnvironment[]> = new Map();
  latestDeployments: Map<String, IDeployment> = new Map();
  navLinks: any[];
  activeLinkIndex = -1;
  prettyPrintVersion = prettyPrintVersion;
  isWriter = false;
  @ViewChild("environmentGrid") environmentGrid?: ElementRef;

  filterForm = this.fb.group({
    asOfTime: [null, [Validators.required]],
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar,
    private translateService: TranslateService,
    protected environmentGroupService: EnvironmentGroupService,
    protected productService: ProductService,
    protected environmentService: EnvironmentService,
    protected deploymentService: DeploymentService,
    private loginModalService: LoginModalService) {
    this.navLinks = [
      {
        label: 'Dashboard',
        link: '/dashboard',
        index: 0
      }, {
        label: 'Table',
        link: '/table',
        index: 1
      },
    ];
  }

  ngOnInit(): void {
    const url = this.router.url + "?";
    const pos = url.indexOf('?');
    const route = url.substring(0, pos);
    this.activeLinkIndex = this.navLinks.indexOf(this.navLinks.find(tab => tab.link === route ));
    this.route.queryParams.subscribe(params => {
        this.filterForm.setValue({"asOfTime": params && params.asOf ? params.asOf : ""});
        this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
          this.account = account;
          this.isWriter = account?.authorities.includes(Authority.WRITER) || false;
          if (account) {
            this.loadAll();
          }
        });
      });
    this.router.routeReuseStrategy.shouldReuseRoute = ():boolean => {
      return false;
    };

    this.router.events.subscribe((evt) => {
      if (evt instanceof NavigationEnd) {
        this.router.navigated = false;
        window.scrollTo(0, 0);
      }
    });
  }

  loadAll(): void {
    this.environmentGroupService.query({size: 1000}).subscribe((res: HttpResponse<IEnvironmentGroup[]>) => {
      res.body?.sort((l, r) => (l.sortOrder || 0) - (r.sortOrder || 0));
      this.environmentGroups = (res.body || []).filter(eg => !eg.hidden);
      this.buildProductsForEnvs();
    });
    this.productService.query({size: 1000}).subscribe((res: HttpResponse<IProduct[]>) => {
      this.products = res.body || [];
      this.products.forEach(product => {
        const initials = product.fullName?.split(" ") ||[];
        if (initials.length >= 2) {
          product.initials = initials[0].substring(0, 1).toUpperCase() + initials[1].substring(0, 1).toUpperCase();
        } else {
          product.initials = product.shortName?.substring(0, 2).toUpperCase();
        }
      });
      this.buildProductsForEnvs();
    });
    this.environmentService.query({size: 1000}).subscribe((res: HttpResponse<IEnvironment[]>) => {
      this.allEnvironments = res.body || undefined;
      this.environments = new Map();
      if (res.body) {
        res.body.sort((l, r) => {
          if (l.environmentGroupSortOrder !== r.environmentGroupSortOrder) {
            return (l.environmentGroupSortOrder || 0) - (r.environmentGroupSortOrder || 0);
          }
          return (l.sortOrder || 0) - (r.sortOrder || 0);
        });
        res.body.forEach(environment => {
          const shortname = environment.environmentGroupShortName || "";
          const envs = this.environments.get(shortname);
          if (envs) {
            envs.push(environment);
          } else {
            this.environments.set(shortname, [environment])
          }
        });
        this.buildProductsForEnvs();
      }
    });
    const asOf = this.filterForm.get(['asOfTime'])!.value ? moment(this.filterForm.get(['asOfTime'])!.value, DATE_TIME_FORMAT).toJSON() : undefined
    this.deploymentService
      .latest(asOf ? { asOf } : undefined)
      .subscribe(
        (res: HttpResponse<IDeployment[]>) => {
          if (res.body != null) {
            res.body.forEach(deployment => {
              const key = deployment.environmentShortName + "|" + deployment.product?.shortName;
              if (deployment.status !== 'DELETED') {
                this.latestDeployments.set(key, deployment);
              }
            });
            // eslint-disable-next-line no-console
            // console.log(this.latestDeployments);
            // eslint-disable-next-line no-console
            // console.log(this.latestDeployments.get("INT|commons")?.productVersionVersion);
            this.buildProductsForEnvs();
          }
        }
      );
  }

  update(): void {
    this.router.navigate([this.navLinks[this.activeLinkIndex].link], { queryParams: { asOf: this.filterForm.get(['asOfTime'])!.value }});
  }

  // Build the list of all products installed on each environment group
  buildProductsForEnvs(): void {
    // Check we have fully loaded everything
    if (!this.products || !this.environmentGroups
      || this.products.length === 0 || this.environmentGroups.length === 0
      || this.environments.size === 0 || this.latestDeployments.size === 0) {
      return;
    }

    this.environmentGroups.forEach((environmentGroup: IEnvironmentGroup) => {
      const productsForThisEnvGroup: IProduct[] = [];
      this.getEnvironments(environmentGroup.shortName).forEach((environment: IEnvironment) => {
        this.products?.forEach((product: IProduct) => {
          if (this.latestDeployments.get(environment.shortName + '|' + product.shortName) != null) {
            if (!productsForThisEnvGroup.includes(product)) {
              productsForThisEnvGroup.push(product);
            }
          }
        });
      });
      this.productsForEnvGroups.set(environmentGroup.shortName || "", productsForThisEnvGroup);
    });
  }

  getProducts(shortName?: string): IProduct[] {
    return this.productsForEnvGroups.get(shortName || '') || [];
  }

  getEnvironments(shortName?: string): IEnvironment[] {
    return this.environments.get(shortName || '') || [];
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  @HostListener('dragstart', ['$event'])
  dragstart(event: Event): void {
    if (!this.isWriter) {
      event.preventDefault();
    }
  }

  onEnvironmentGroupsSorted(event: CdkDragDrop<IEnvironmentGroup>): void {
    if (this.environmentGroups) {
      moveItemInArray(this.environmentGroups, event.previousIndex, event.currentIndex);
      const patch: IEnvironmentGroup[] = [];
      this.environmentGroups.forEach((environmentGroup: IEnvironmentGroup, index:number) => {
        const thisEnvironmentGroupPatch = new EnvironmentGroup();
        environmentGroup.sortOrder = index;
        thisEnvironmentGroupPatch.id = environmentGroup.id;
        thisEnvironmentGroupPatch.sortOrder = index;
        patch.push(thisEnvironmentGroupPatch);
      });
      this.environmentGroupService.bulkPatch(patch).subscribe();
    }
  }

  onEnvironmentsSorted(event: NgsgOrderChange<IEnvironment>): void {
    const patch: IEnvironment[] = [];
    event.currentOrder.forEach((environment: IEnvironment, index:number) => {
      const thisEnvironmentPatch = new Environment();
      environment.sortOrder = index;
      thisEnvironmentPatch.id = environment.id;
      thisEnvironmentPatch.sortOrder = index;
      patch.push(thisEnvironmentPatch);
    });
    this.environmentService.bulkPatch(patch).subscribe();
  }

  login(): void {
    this.loginModalService.open();
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  copyTags(environment: IEnvironment): boolean {
    let versions = "";
    this.products?.forEach(product => {
      const deployment = this.latestDeployments.get(environment.shortName + '|' + product.shortName);
      if (deployment) {
        versions = versions + product.shortName + ": " + deployment.productVersionVersion + "\n";
      }
    });
    if (versions !== "") {
      this.clipboard.copy(versions);
      this.snackBar.open(this.translateService.instant("home.versionCopied"), "", { duration: 2000 });
    } else {
      this.snackBar.open(this.translateService.instant("home.versionNotCopied"), "", { duration: 2000 });
    }
    return false;
  }
}
