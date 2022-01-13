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
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IDeployment, Deployment } from 'app/shared/model/deployment.model';
import { DeploymentService } from './deployment.service';
import { IProductVersion } from 'app/shared/model/product-version.model';
import { ProductVersionService } from 'app/entities/product-version/product-version.service';
import { IEnvironment } from 'app/shared/model/environment.model';
import { EnvironmentService } from 'app/entities/environment/environment.service';
import { IBuild } from 'app/shared/model/build.model';
import { BuildService } from 'app/entities/build/build.service';

type SelectableEntity = IProductVersion | IEnvironment | IBuild;

@Component({
  selector: 'jhi-deployment-update',
  templateUrl: './deployment-update.component.html',
})
export class DeploymentUpdateComponent implements OnInit {
  isSaving = false;
  productversions: IProductVersion[] = [];
  environments: IEnvironment[] = [];
  builds: IBuild[] = [];

  editForm = this.fb.group({
    id: [],
    startTime: [null, [Validators.required]],
    endTime: [],
    user: [],
    status: [null, [Validators.required]],
    namespace: [],
    url: [],
    testUrl: [],
    productVersionId: [null, Validators.required],
    environmentId: [null, Validators.required],
    buildId: [],
  });

  constructor(
    protected deploymentService: DeploymentService,
    protected productVersionService: ProductVersionService,
    protected environmentService: EnvironmentService,
    protected buildService: BuildService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ deployment }) => {
      if (!deployment.id) {
        const today = moment().startOf('day');
        deployment.startTime = today;
        deployment.endTime = today;
      }

      this.updateForm(deployment);

      this.productVersionService.query().subscribe((res: HttpResponse<IProductVersion[]>) => (this.productversions = res.body || []));

      this.environmentService.query().subscribe((res: HttpResponse<IEnvironment[]>) => (this.environments = res.body || []));

      this.buildService.query().subscribe((res: HttpResponse<IBuild[]>) => (this.builds = res.body || []));
    });
  }

  updateForm(deployment: IDeployment): void {
    this.editForm.patchValue({
      id: deployment.id,
      startTime: deployment.startTime ? deployment.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: deployment.endTime ? deployment.endTime.format(DATE_TIME_FORMAT) : null,
      user: deployment.user,
      status: deployment.status,
      namespace: deployment.namespace,
      url: deployment.url,
      testUrl: deployment.testUrl,
      productVersionId: deployment.productVersionId,
      environmentId: deployment.environmentId,
      buildId: deployment.buildId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const deployment = this.createFromForm();
    if (deployment.id !== undefined) {
      this.subscribeToSaveResponse(this.deploymentService.update(deployment));
    } else {
      this.subscribeToSaveResponse(this.deploymentService.create(deployment));
    }
  }

  private createFromForm(): IDeployment {
    return {
      ...new Deployment(),
      id: this.editForm.get(['id'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? moment(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? moment(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      user: this.editForm.get(['user'])!.value,
      status: this.editForm.get(['status'])!.value,
      namespace: this.editForm.get(['namespace'])!.value,
      url: this.editForm.get(['url'])!.value,
      testUrl: this.editForm.get(['testUrl'])!.value,
      productVersionId: this.editForm.get(['productVersionId'])!.value,
      environmentId: this.editForm.get(['environmentId'])!.value,
      buildId: this.editForm.get(['buildId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDeployment>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
