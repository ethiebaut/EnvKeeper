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

import {Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit} from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router, Data } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDeployment } from 'app/shared/model/deployment.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { DeploymentService } from './deployment.service';
import { DeploymentDeleteDialogComponent } from './deployment-delete-dialog.component';
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";
import { prettyPrintIntervalMS } from "app/shared/util/pretty-print-interval";
import {IEnvironment} from "../../shared/model/environment.model";
import {EnvironmentService} from "../environment/environment.service";

@Component({
  selector: 'jhi-deployment',
  templateUrl: './deployment.component.html',
})
export class DeploymentComponent implements OnInit, AfterViewInit, OnDestroy {
  deployments?: IDeployment[];
  environments?: IEnvironment[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  prettyPrintVersion = prettyPrintVersion;
  prettyPrintIntervalMS = prettyPrintIntervalMS;
  filterEnabled = false;
  selectedEnvironmentId?: number;
  selectedStatus?: string;
  deploymentStatuses = ["IN_PROGRESS", "DELETED", "SUCCEEDED", "FAILED_KEPT", "FAILED_ROLLED_BACK", "VERIFIED"];
  pageLoadRequestNumber = 0;

  @ViewChild('environmentSelect', {static: false})
  public environementDropdown?: ElementRef;
  @ViewChild('statusSelect', {static: false})
  public statusDropdown?: ElementRef;

  constructor(
    protected deploymentService: DeploymentService,
    protected environmentService: EnvironmentService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {
    this.environmentService
      .query({
        sort: ["shortName"],
      })
      .subscribe(
        (res: HttpResponse<IEnvironment[]>) => {
          this.environments = res.body || [];
          if (this.selectedEnvironmentId) {
            setTimeout(() => (this.environementDropdown as any).nativeElement.value = this.selectedEnvironmentId, 0);
          }
        },
        () => this.onError()
      );
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    const pageToLoad: number = page || this.page || 1;

    const thisPageLoadRequestNumber = ++this.pageLoadRequestNumber;
    this.deploymentService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
        status: {equals: this.selectedStatus || ''},
        environmentId: {equals: this.selectedEnvironmentId || ''},
      })
      .subscribe(
        (res: HttpResponse<IDeployment[]>) => {
          if (thisPageLoadRequestNumber === this.pageLoadRequestNumber) {
            this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
          }
        },
        () => this.onError()
      );
  }

  ngOnInit(): void {
    this.handleNavigation();
    this.registerChangeInDeployments();
  }

  ngAfterViewInit(): void {
    if (this.selectedStatus) {
      (this.statusDropdown as any).nativeElement.value = this.selectedStatus;
    }
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      this.selectedEnvironmentId = params.get('environmentId') ? Number(params.get('environmentId')) : undefined;
      this.selectedStatus = params.get('status') || undefined;
      this.filterEnabled = !!this.selectedEnvironmentId || !!this.selectedStatus;
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

  protected onSuccess(data: IDeployment[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/deployment'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
          environmentId: this.selectedEnvironmentId,
          status: this.selectedStatus
        },
      });
    }
    this.deployments = data || [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  public toggleFilter(): void {
    this.filterEnabled = !this.filterEnabled;
    if (!this.filterEnabled && (this.selectedEnvironmentId || this.selectedStatus)) {
      this.selectedEnvironmentId = undefined;
      this.selectedStatus = undefined;
      if (this.environementDropdown) {
        (this.environementDropdown as any).nativeElement.selectedIndex = -1;
      }
      if (this.statusDropdown) {
        (this.statusDropdown as any).nativeElement.selectedIndex = -1;
      }
      this.loadPage();
    }
  }

  public onEnvironmentSelected(environment: string): void {
    const environmentId = Number(environment);
    this.selectedEnvironmentId = environmentId;
    this.loadPage();
  }

  public onStatusSelected(status: string): void {
    this.selectedStatus = status;
    this.loadPage();
  }
}
