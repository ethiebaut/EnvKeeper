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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Data, ParamMap, Router} from '@angular/router';

import {IEnvironment} from 'app/shared/model/environment.model';
import {HttpHeaders, HttpResponse} from "@angular/common/http";
import {DeploymentService} from "../deployment/deployment.service";
import {IDeployment} from "../../shared/model/deployment.model";
import {combineLatest, Subscription} from "rxjs";
import {ITEMS_PER_PAGE} from "../../shared/constants/pagination.constants";
import {JhiEventManager} from "ng-jhipster";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {DeploymentDeleteDialogComponent} from "../deployment/deployment-delete-dialog.component";
import {ProductService} from "../product/product.service";
import {IDeploymentTable} from "../../shared/model/deployment-table.model";
import {Product} from "../../shared/model/product.model";
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";
import {IDeploymentTableCell} from "../../shared/model/deployment-table-cell.model";

@Component({
  selector: 'jhi-environment-history',
  templateUrl: './environment-history.component.html',
})
export class EnvironmentHistoryComponent implements OnInit, OnDestroy {
  environment: IEnvironment | null = null;
  deployments?: IDeployment[];
  deploymentTable?: IDeploymentTable[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  productId?: string;
  productName?: string;
  navLinks: any[];
  activeLinkIndex = -1;
  products?: Product[];
  prettyPrintVersion = prettyPrintVersion;

  constructor(
    protected deploymentService: DeploymentService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {
    this.navLinks = [
      {
        label: 'List',
        link: '/history/list',
        index: 0
      }, {
        label: 'Table',
        link: '/history/table',
        index: 1
      },
    ];
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;

    if (this.activeLinkIndex === 0) {
      this.deploymentService
        .query({
          environmentId: {equals: this.environment?.id},
          productId: {equals: this.productId || ""},
          page: pageToLoad - 1,
          size: this.itemsPerPage,
          sort: ['startTime,desc'],
        })
        .subscribe(
          (res: HttpResponse<IDeployment[]>) => this.onDeploymentSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
          () => this.onError()
        );
    } else {
      this.deploymentService
        .queryTable({
          environmentId: {equals: this.environment?.id},
          productId: {equals: this.productId || ""},
          page: pageToLoad - 1,
          size: this.itemsPerPage,
          sort: ['startTime,desc'],
        })
        .subscribe(
          (res: HttpResponse<IDeploymentTable[]>) => this.onDeploymentTableSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
          () => this.onError()
        );
    }
  }

  ngOnInit(): void {
    this.activeLinkIndex = this.navLinks.indexOf(this.navLinks.find(tab => this.router.url.includes(tab.link)));
    this.activatedRoute.data.subscribe(({ environment }) => {
      this.environment = environment;
      this.handleNavigation();
      this.registerChangeInDeployments();
    });
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      this.productId = params.get('productId') || undefined;
      if (this.productId) {
        this.productService.find(+this.productId).subscribe(received => {
          if (received.body) {
            this.productName = received.body.fullName;
          }
        });
      }
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    }).subscribe();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IDeployment): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInDeployments(): void {
    this.eventSubscriber = this.eventManager.subscribe('deploymentListModification', () => this.loadPage());
  }

  delete(deployment: IDeployment): void {
    const modalRef = this.modalService.open(DeploymentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.deployment = deployment;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  getUpdatedProduct(deploymentRow: IDeploymentTable): IDeploymentTableCell | undefined {
    for (const deploymentTableCell of deploymentRow.deploymentTableCells || []) {
      if (deploymentTableCell.productVersionId !== deploymentTableCell.previousProductVersionId) {
        return deploymentTableCell;
      }
    }
    return undefined;
  }

  protected onDeploymentSuccess(data: IDeployment[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/environment/' + this.environment?.id + '/history/list'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
          productId: this.productId || '',
        },
      });
    }
    this.deployments = data || [];
    this.ngbPaginationPage = this.page;
  }

  protected onDeploymentTableSuccess(data: IDeploymentTable[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/environment/' + this.environment?.id + '/history/table'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
          productId: this.productId || '',
        },
      });
    }
    this.deploymentTable = data || [];
    this.ngbPaginationPage = this.page;
    this.products = [];
    if (this.deploymentTable !== null && this.deploymentTable.length > 0) {
      this.deploymentTable[0].deploymentTableCells?.forEach(deploymentTableCell => {
        this.products?.push(deploymentTableCell.product || {shortName: "Unknown"});
      });
    }
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  previousState(): void {
    window.history.back();
  }
}
