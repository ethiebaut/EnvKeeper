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

import { IProductComponentVersion, ProductComponentVersion } from 'app/shared/model/product-component-version.model';
import { ProductComponentVersionService } from './product-component-version.service';
import { IProductComponent } from 'app/shared/model/product-component.model';
import { ProductComponentService } from 'app/entities/product-component/product-component.service';

@Component({
  selector: 'jhi-product-component-version-update',
  templateUrl: './product-component-version-update.component.html',
})
export class ProductComponentVersionUpdateComponent implements OnInit {
  isSaving = false;
  productcomponents: IProductComponent[] = [];

  editForm = this.fb.group({
    id: [],
    version: [null, [Validators.required]],
    buildStatus: [null, [Validators.required]],
    startTime: [null, [Validators.required]],
    endTime: [],
    buildUrl: [],
    releaseNotes: [],
    componentId: [null, Validators.required],
  });

  constructor(
    protected productComponentVersionService: ProductComponentVersionService,
    protected productComponentService: ProductComponentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productComponentVersion }) => {
      if (!productComponentVersion.id) {
        const today = moment().startOf('day');
        productComponentVersion.startTime = today;
        productComponentVersion.endTime = today;
      }

      this.updateForm(productComponentVersion);

      this.productComponentService.query().subscribe((res: HttpResponse<IProductComponent[]>) => (this.productcomponents = res.body || []));
    });
  }

  updateForm(productComponentVersion: IProductComponentVersion): void {
    this.editForm.patchValue({
      id: productComponentVersion.id,
      version: productComponentVersion.version,
      buildStatus: productComponentVersion.buildStatus,
      startTime: productComponentVersion.startTime ? productComponentVersion.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: productComponentVersion.endTime ? productComponentVersion.endTime.format(DATE_TIME_FORMAT) : null,
      buildUrl: productComponentVersion.buildUrl,
      releaseNotes: productComponentVersion.releaseNotes,
      componentId: productComponentVersion.componentId,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productComponentVersion = this.createFromForm();
    if (productComponentVersion.id !== undefined) {
      this.subscribeToSaveResponse(this.productComponentVersionService.update(productComponentVersion));
    } else {
      this.subscribeToSaveResponse(this.productComponentVersionService.create(productComponentVersion));
    }
  }

  private createFromForm(): IProductComponentVersion {
    return {
      ...new ProductComponentVersion(),
      id: this.editForm.get(['id'])!.value,
      version: this.editForm.get(['version'])!.value,
      buildStatus: this.editForm.get(['buildStatus'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? moment(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? moment(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      buildUrl: this.editForm.get(['buildUrl'])!.value,
      releaseNotes: this.editForm.get(['releaseNotes'])!.value,
      componentId: this.editForm.get(['componentId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductComponentVersion>>): void {
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

  trackById(index: number, item: IProductComponent): any {
    return item.id;
  }
}
