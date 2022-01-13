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
import { EnvironmentUpdateComponent } from 'app/entities/environment/environment-update.component';
import { EnvironmentService } from 'app/entities/environment/environment.service';
import { Environment } from 'app/shared/model/environment.model';

describe('Component Tests', () => {
  describe('Environment Management Update Component', () => {
    let comp: EnvironmentUpdateComponent;
    let fixture: ComponentFixture<EnvironmentUpdateComponent>;
    let service: EnvironmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [EnvironmentUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(EnvironmentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EnvironmentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(EnvironmentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Environment(123);
        entity.sortOrder = 1;
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
        const entity = new Environment();
        entity.sortOrder = 1;
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
