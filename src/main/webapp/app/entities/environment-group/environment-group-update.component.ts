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

import { IEnvironmentGroup, EnvironmentGroup } from 'app/shared/model/environment-group.model';
import { EnvironmentGroupService } from './environment-group.service';

@Component({
  selector: 'jhi-environment-group-update',
  templateUrl: './environment-group-update.component.html',
})
export class EnvironmentGroupUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    shortName: [null, [Validators.required]],
    fullName: [null, [Validators.required]],
    description: [null, [Validators.required]],
    sortOrder: [],
    hidden: [null, [Validators.required]],
  });

  constructor(
    protected environmentGroupService: EnvironmentGroupService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ environmentGroup }) => {
      this.updateForm(environmentGroup);
    });
  }

  updateForm(environmentGroup: IEnvironmentGroup): void {
    this.editForm.patchValue({
      id: environmentGroup.id,
      shortName: environmentGroup.shortName,
      fullName: environmentGroup.fullName,
      description: environmentGroup.description,
      sortOrder: (typeof environmentGroup.sortOrder === 'undefined') ? 999999 : environmentGroup.sortOrder,
      hidden: environmentGroup.hidden,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const environmentGroup = this.createFromForm();
    if (environmentGroup.id !== undefined) {
      this.subscribeToSaveResponse(this.environmentGroupService.update(environmentGroup));
    } else {
      this.subscribeToSaveResponse(this.environmentGroupService.create(environmentGroup));
    }
  }

  private createFromForm(): IEnvironmentGroup {
    return {
      ...new EnvironmentGroup(),
      id: this.editForm.get(['id'])!.value,
      shortName: this.editForm.get(['shortName'])!.value,
      fullName: this.editForm.get(['fullName'])!.value,
      description: this.editForm.get(['description'])!.value,
      sortOrder: this.editForm.get(['sortOrder'])!.value,
      hidden: this.editForm.get(['hidden'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnvironmentGroup>>): void {
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
}
