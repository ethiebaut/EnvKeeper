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

import { IProductComponent, ProductComponent } from 'app/shared/model/product-component.model';
import { ProductComponentService } from './product-component.service';

@Component({
  selector: 'jhi-product-component-update',
  templateUrl: './product-component-update.component.html',
})
export class ProductComponentUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    shortName: [null, [Validators.required]],
    fullName: [null, [Validators.required]],
    description: [null, [Validators.required]],
  });

  constructor(
    protected productComponentService: ProductComponentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productComponent }) => {
      this.updateForm(productComponent);
    });
  }

  updateForm(productComponent: IProductComponent): void {
    this.editForm.patchValue({
      id: productComponent.id,
      shortName: productComponent.shortName,
      fullName: productComponent.fullName,
      description: productComponent.description,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productComponent = this.createFromForm();
    if (productComponent.id !== undefined) {
      this.subscribeToSaveResponse(this.productComponentService.update(productComponent));
    } else {
      this.subscribeToSaveResponse(this.productComponentService.create(productComponent));
    }
  }

  private createFromForm(): IProductComponent {
    return {
      ...new ProductComponent(),
      id: this.editForm.get(['id'])!.value,
      shortName: this.editForm.get(['shortName'])!.value,
      fullName: this.editForm.get(['fullName'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductComponent>>): void {
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
