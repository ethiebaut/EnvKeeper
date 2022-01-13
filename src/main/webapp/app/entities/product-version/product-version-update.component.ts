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

import { IProductVersion, ProductVersion } from 'app/shared/model/product-version.model';
import { ProductVersionService } from './product-version.service';
import { IProduct } from 'app/shared/model/product.model';
import { ProductService } from 'app/entities/product/product.service';
import { IBuild } from 'app/shared/model/build.model';
import { BuildService } from 'app/entities/build/build.service';
import { IProductComponentVersion } from 'app/shared/model/product-component-version.model';
import { ProductComponentVersionService } from 'app/entities/product-component-version/product-component-version.service';

type SelectableEntity = IProduct | IBuild | IProductComponentVersion;

@Component({
  selector: 'jhi-product-version-update',
  templateUrl: './product-version-update.component.html',
})
export class ProductVersionUpdateComponent implements OnInit {
  isSaving = false;
  products: IProduct[] = [];
  builds: IBuild[] = [];
  productcomponentversions: IProductComponentVersion[] = [];

  editForm = this.fb.group({
    id: [],
    version: [null, [Validators.required]],
    releaseNotes: [],
    productId: [null, Validators.required],
    buildId: [],
    components: [],
  });

  constructor(
    protected productVersionService: ProductVersionService,
    protected productService: ProductService,
    protected buildService: BuildService,
    protected productComponentVersionService: ProductComponentVersionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVersion }) => {
      this.updateForm(productVersion);

      this.productService.query().subscribe((res: HttpResponse<IProduct[]>) => (this.products = res.body || []));

      this.buildService.query().subscribe((res: HttpResponse<IBuild[]>) => (this.builds = res.body || []));

      this.productComponentVersionService
        .query()
        .subscribe((res: HttpResponse<IProductComponentVersion[]>) => (this.productcomponentversions = res.body || []));
    });
  }

  updateForm(productVersion: IProductVersion): void {
    this.editForm.patchValue({
      id: productVersion.id,
      version: productVersion.version,
      releaseNotes: productVersion.releaseNotes,
      productId: productVersion.productId,
      buildId: productVersion.buildId,
      components: productVersion.components,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productVersion = this.createFromForm();
    if (productVersion.id !== undefined) {
      this.subscribeToSaveResponse(this.productVersionService.update(productVersion));
    } else {
      this.subscribeToSaveResponse(this.productVersionService.create(productVersion));
    }
  }

  private createFromForm(): IProductVersion {
    return {
      ...new ProductVersion(),
      id: this.editForm.get(['id'])!.value,
      version: this.editForm.get(['version'])!.value,
      releaseNotes: this.editForm.get(['releaseNotes'])!.value,
      productId: this.editForm.get(['productId'])!.value,
      buildId: this.editForm.get(['buildId'])!.value,
      components: this.editForm.get(['components'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductVersion>>): void {
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

  getSelected(selectedVals: IProductComponentVersion[], option: IProductComponentVersion): IProductComponentVersion {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
