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

import { IBuildStatistic, BuildStatistic } from 'app/shared/model/build-statistic.model';
import { BuildStatisticService } from './build-statistic.service';
import { IBuild } from 'app/shared/model/build.model';
import { BuildService } from 'app/entities/build/build.service';

@Component({
  selector: 'jhi-build-statistic-update',
  templateUrl: './build-statistic-update.component.html',
})
export class BuildStatisticUpdateComponent implements OnInit {
  isSaving = false;
  builds: IBuild[] = [];

  editForm = this.fb.group({
    id: [],
    key: [null, [Validators.required]],
    value: [null, [Validators.required]],
    buildId: [null, Validators.required],
  });

  constructor(
    protected buildStatisticService: BuildStatisticService,
    protected buildService: BuildService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ buildStatistic }) => {
      this.updateForm(buildStatistic);

      this.buildService.query().subscribe((res: HttpResponse<IBuild[]>) => (this.builds = res.body || []));
    });
  }

  updateForm(buildStatistic: IBuildStatistic): void {
    this.editForm.patchValue({
      id: buildStatistic.id,
      key: buildStatistic.key,
      value: buildStatistic.value,
      buildId: buildStatistic.buildId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const buildStatistic = this.createFromForm();
    if (buildStatistic.id !== undefined) {
      this.subscribeToSaveResponse(this.buildStatisticService.update(buildStatistic));
    } else {
      this.subscribeToSaveResponse(this.buildStatisticService.create(buildStatistic));
    }
  }

  private createFromForm(): IBuildStatistic {
    return {
      ...new BuildStatistic(),
      id: this.editForm.get(['id'])!.value,
      key: this.editForm.get(['key'])!.value,
      value: this.editForm.get(['value'])!.value,
      buildId: this.editForm.get(['buildId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBuildStatistic>>): void {
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
