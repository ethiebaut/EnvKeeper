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

import { IBuildStep, BuildStep } from 'app/shared/model/build-step.model';
import { BuildStepService } from './build-step.service';
import { IBuild } from 'app/shared/model/build.model';
import { BuildService } from 'app/entities/build/build.service';

@Component({
  selector: 'jhi-build-step-update',
  templateUrl: './build-step-update.component.html',
})
export class BuildStepUpdateComponent implements OnInit {
  isSaving = false;
  builds: IBuild[] = [];

  editForm = this.fb.group({
    id: [],
    step: [null, [Validators.required]],
    status: [null, [Validators.required]],
    startTime: [null, [Validators.required]],
    endTime: [],
    buildId: [null, Validators.required],
  });

  constructor(
    protected buildStepService: BuildStepService,
    protected buildService: BuildService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ buildStep }) => {
      if (!buildStep.id) {
        const today = moment().startOf('day');
        buildStep.startTime = today;
        buildStep.endTime = today;
      }

      this.updateForm(buildStep);

      this.buildService.query().subscribe((res: HttpResponse<IBuild[]>) => (this.builds = res.body || []));
    });
  }

  updateForm(buildStep: IBuildStep): void {
    this.editForm.patchValue({
      id: buildStep.id,
      step: buildStep.step,
      status: buildStep.status,
      startTime: buildStep.startTime ? buildStep.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: buildStep.endTime ? buildStep.endTime.format(DATE_TIME_FORMAT) : null,
      buildId: buildStep.buildId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const buildStep = this.createFromForm();
    if (buildStep.id !== undefined) {
      this.subscribeToSaveResponse(this.buildStepService.update(buildStep));
    } else {
      this.subscribeToSaveResponse(this.buildStepService.create(buildStep));
    }
  }

  private createFromForm(): IBuildStep {
    return {
      ...new BuildStep(),
      id: this.editForm.get(['id'])!.value,
      step: this.editForm.get(['step'])!.value,
      status: this.editForm.get(['status'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? moment(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? moment(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      buildId: this.editForm.get(['buildId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBuildStep>>): void {
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

  trackById(index: number, item: IBuild): any {
    return item.id;
  }
}
