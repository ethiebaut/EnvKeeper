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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductVersion } from 'app/shared/model/product-version.model';
import {HttpResponse} from "@angular/common/http";
import {IDeployment} from "../../shared/model/deployment.model";
import {DeploymentService} from "../deployment/deployment.service";
import {DeploymentDeleteDialogComponent} from "../deployment/deployment-delete-dialog.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";
import {ITicket} from "../../shared/model/ticket.model";
import {TicketService} from "../ticket/ticket.service";

@Component({
  selector: 'jhi-product-version-detail',
  templateUrl: './product-version-detail.component.html',
})
export class ProductVersionDetailComponent implements OnInit {
  productVersion: IProductVersion | null = null;
  deployments?: IDeployment[];
  tickets?: ITicket[];
  prettyPrintVersion = prettyPrintVersion;

  constructor(
    protected deploymentService: DeploymentService,
    protected ticketService: TicketService,
    protected activatedRoute: ActivatedRoute,
    protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVersion }) => {
      this.productVersion = productVersion;
      if (this.productVersion?.releaseNotes) {
        this.productVersion.releaseNotes =
          (this.productVersion.releaseNotes || '').replace(/\\n/g, "\n");
      }
      if (this.productVersion?.components) {
        this.productVersion.components.sort(
          (a, b) => ((a.componentShortName || '').localeCompare(b.componentShortName || '')))
      }
    });

    this.deploymentService
      .latest()
      .subscribe(
        (res: HttpResponse<IDeployment[]>) => {
          this.deployments = [];
          if (res.body != null) {
            res.body.forEach(deployment => {
              if (deployment.status !== 'DELETED' && deployment.productVersionId === this.productVersion?.id) {
                if (this.deployments) {
                  this.deployments.push(deployment);
                }
              }
            });
            // eslint-disable-next-line no-console
            // console.log(this.latestDeployments);
            // eslint-disable-next-line no-console
            // console.log(this.latestDeployments.get("INT|commons")?.productVersionVersion);
          }
        }
      );

    this.ticketService
      .query({
        fixedInId: {equals: this.productVersion?.id},
        page: 0,
        size: 1000,
        sort: ["externalId"],
      })
      .subscribe(
        (res: HttpResponse<ITicket[]>) => this.tickets = res.body || []
      );
  }

  previousState(): void {
    window.history.back();
  }

  deleteDeployment(deployment: IDeployment): void {
    const modalRef = this.modalService.open(DeploymentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.deployment = deployment;
  }
}
