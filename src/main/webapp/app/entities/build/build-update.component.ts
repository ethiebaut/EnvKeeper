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

import { IBuild, Build } from 'app/shared/model/build.model';
import { BuildService } from './build.service';

@Component({
  selector: 'jhi-build-update',
  templateUrl: './build-update.component.html',
})
export class BuildUpdateComponent implements OnInit {
  isSaving = false;
  builds: IBuild[] = [];

  editForm = this.fb.group({
    id: [],
    buildUrl: [null, [Validators.required]],
    buildName: [],
    status: [null, [Validators.required]],
    startTime: [null, [Validators.required]],
    endTime: [],
    parentBuildId: [],
  });

  constructor(protected buildService: BuildService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ build }) => {
      if (!build.id) {
        const today = moment().startOf('day');
        build.startTime = today;
        build.endTime = today;
      }

      this.updateForm(build);

      this.buildService.query({id: {notEquals: build.id}}).subscribe((res: HttpResponse<IBuild[]>) => (this.builds = res.body || []));
    });
  }

  updateForm(build: IBuild): void {
    this.editForm.patchValue({
      id: build.id,
      buildUrl: build.buildUrl,
      buildName: build.buildName,
      status: build.status,
      startTime: build.startTime ? build.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: build.endTime ? build.endTime.format(DATE_TIME_FORMAT) : null,
      parentBuildId: build.parentBuildId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const build = this.createFromForm();
    if (build.id !== undefined) {
      this.subscribeToSaveResponse(this.buildService.update(build));
    } else {
      this.subscribeToSaveResponse(this.buildService.create(build));
    }
  }

  private createFromForm(): IBuild {
    return {
      ...new Build(),
      id: this.editForm.get(['id'])!.value,
      buildUrl: this.editForm.get(['buildUrl'])!.value,
      buildName: this.editForm.get(['buildName'])!.value,
      status: this.editForm.get(['status'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? moment(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? moment(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      parentBuildId: this.editForm.get(['parentBuildId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBuild>>): void {
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
