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

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnvKeeperTestModule } from '../../../test.module';
import { ProductVersionDetailComponent } from 'app/entities/product-version/product-version-detail.component';
import { ProductVersion } from 'app/shared/model/product-version.model';

describe('Component Tests', () => {
  describe('ProductVersion Management Detail Component', () => {
    let comp: ProductVersionDetailComponent;
    let fixture: ComponentFixture<ProductVersionDetailComponent>;
    const route = ({ data: of({ productVersion: new ProductVersion(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [ProductVersionDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(ProductVersionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ProductVersionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load productVersion on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.productVersion).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
