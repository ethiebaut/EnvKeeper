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

import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { EnvKeeperTestModule } from '../../../test.module';
import { ProductComponentVersionUpdateComponent } from 'app/entities/product-component-version/product-component-version-update.component';
import { ProductComponentVersionService } from 'app/entities/product-component-version/product-component-version.service';
import { ProductComponentVersion } from 'app/shared/model/product-component-version.model';

describe('Component Tests', () => {
  describe('ProductComponentVersion Management Update Component', () => {
    let comp: ProductComponentVersionUpdateComponent;
    let fixture: ComponentFixture<ProductComponentVersionUpdateComponent>;
    let service: ProductComponentVersionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [ProductComponentVersionUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ProductComponentVersionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProductComponentVersionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ProductComponentVersionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ProductComponentVersion(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new ProductComponentVersion();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
