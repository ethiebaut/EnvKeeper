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

import { IBuildStepStatistic, BuildStepStatistic } from 'app/shared/model/build-step-statistic.model';
import { BuildStepStatisticService } from './build-step-statistic.service';
import { IBuildStep } from 'app/shared/model/build-step.model';
import { BuildStepService } from 'app/entities/build-step/build-step.service';

@Component({
  selector: 'jhi-build-step-statistic-update',
  templateUrl: './build-step-statistic-update.component.html',
})
export class BuildStepStatisticUpdateComponent implements OnInit {
  isSaving = false;
  buildsteps: IBuildStep[] = [];

  editForm = this.fb.group({
    id: [],
    key: [null, [Validators.required]],
    value: [null, [Validators.required]],
    buildStepId: [null, Validators.required],
  });

  constructor(
    protected buildStepStatisticService: BuildStepStatisticService,
    protected buildStepService: BuildStepService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ buildStepStatistic }) => {
      this.updateForm(buildStepStatistic);

      this.buildStepService.query().subscribe((res: HttpResponse<IBuildStep[]>) => (this.buildsteps = res.body || []));
    });
  }

  updateForm(buildStepStatistic: IBuildStepStatistic): void {
    this.editForm.patchValue({
      id: buildStepStatistic.id,
      key: buildStepStatistic.key,
      value: buildStepStatistic.value,
      buildStepId: buildStepStatistic.buildStepId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const buildStepStatistic = this.createFromForm();
    if (buildStepStatistic.id !== undefined) {
      this.subscribeToSaveResponse(this.buildStepStatisticService.update(buildStepStatistic));
    } else {
      this.subscribeToSaveResponse(this.buildStepStatisticService.create(buildStepStatistic));
    }
  }

  private createFromForm(): IBuildStepStatistic {
    return {
      ...new BuildStepStatistic(),
      id: this.editForm.get(['id'])!.value,
      key: this.editForm.get(['key'])!.value,
      value: this.editForm.get(['value'])!.value,
      buildStepId: this.editForm.get(['buildStepId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBuildStepStatistic>>): void {
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

  trackById(index: number, item: IBuildStep): any {
    return item.id;
  }
}
