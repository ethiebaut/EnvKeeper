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

import { IEnvironment, Environment } from 'app/shared/model/environment.model';
import { EnvironmentService } from './environment.service';
import { IEnvironmentGroup } from 'app/shared/model/environment-group.model';
import { EnvironmentGroupService } from 'app/entities/environment-group/environment-group.service';

@Component({
  selector: 'jhi-environment-update',
  templateUrl: './environment-update.component.html',
})
export class EnvironmentUpdateComponent implements OnInit {
  isSaving = false;
  environmentgroups: IEnvironmentGroup[] = [];

  editForm = this.fb.group({
    id: [],
    shortName: [null, [Validators.required]],
    fullName: [null, [Validators.required]],
    description: [null, [Validators.required]],
    sortOrder: [],
    environmentGroupId: [null, Validators.required],
  });

  constructor(
    protected environmentService: EnvironmentService,
    protected environmentGroupService: EnvironmentGroupService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ environment }) => {
      this.updateForm(environment);

      this.environmentGroupService.query().subscribe((res: HttpResponse<IEnvironmentGroup[]>) => (this.environmentgroups = res.body || []));
    });
  }

  updateForm(environment: IEnvironment): void {
    this.editForm.patchValue({
      id: environment.id,
      shortName: environment.shortName,
      fullName: environment.fullName,
      description: environment.description,
      sortOrder: (typeof environment.sortOrder === 'undefined') ? 999999 : environment.sortOrder,
      environmentGroupId: environment.environmentGroupId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const environment = this.createFromForm();
    if (environment.id !== undefined) {
      this.subscribeToSaveResponse(this.environmentService.update(environment));
    } else {
      this.subscribeToSaveResponse(this.environmentService.create(environment));
    }
  }

  private createFromForm(): IEnvironment {
    return {
      ...new Environment(),
      id: this.editForm.get(['id'])!.value,
      shortName: this.editForm.get(['shortName'])!.value,
      fullName: this.editForm.get(['fullName'])!.value,
      description: this.editForm.get(['description'])!.value,
      sortOrder: this.editForm.get(['sortOrder'])!.value,
      environmentGroupId: this.editForm.get(['environmentGroupId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnvironment>>): void {
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

  trackById(index: number, item: IEnvironmentGroup): any {
    return item.id;
  }
}
