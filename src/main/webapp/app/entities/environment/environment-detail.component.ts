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

import { IEnvironment } from 'app/shared/model/environment.model';
import {IDeployment} from "../../shared/model/deployment.model";
import {DeploymentDeleteDialogComponent} from "../deployment/deployment-delete-dialog.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpResponse} from "@angular/common/http";
import {DeploymentService} from "../deployment/deployment.service";
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";

@Component({
  selector: 'jhi-environment-detail',
  templateUrl: './environment-detail.component.html',
})
export class EnvironmentDetailComponent implements OnInit {
  environment: IEnvironment | null = null;
  deployments: IDeployment[] = [];
  prettyPrintVersion = prettyPrintVersion;

  constructor(
    protected deploymentService: DeploymentService,
    protected activatedRoute: ActivatedRoute,
    protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ environment }) => (this.environment = environment));
    this.deployments = [];
    this.deploymentService
      .latest()
      .subscribe(
        (res: HttpResponse<IDeployment[]>) => {
          if (res.body != null) {
            res.body.forEach(deployment => {
              if (deployment.status !== 'DELETED' && deployment.environmentId === this.environment?.id) {
                this.deployments.push(deployment);
              }
            });
            // eslint-disable-next-line no-console
            // console.log(this.latestDeployments);
            // eslint-disable-next-line no-console
            // console.log(this.latestDeployments.get("INT|commons")?.productVersionVersion);
          }
        }
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
