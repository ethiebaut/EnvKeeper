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
import { BuildStepStatisticUpdateComponent } from 'app/entities/build-step-statistic/build-step-statistic-update.component';
import { BuildStepStatisticService } from 'app/entities/build-step-statistic/build-step-statistic.service';
import { BuildStepStatistic } from 'app/shared/model/build-step-statistic.model';

describe('Component Tests', () => {
  describe('BuildStepStatistic Management Update Component', () => {
    let comp: BuildStepStatisticUpdateComponent;
    let fixture: ComponentFixture<BuildStepStatisticUpdateComponent>;
    let service: BuildStepStatisticService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [BuildStepStatisticUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(BuildStepStatisticUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BuildStepStatisticUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(BuildStepStatisticService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new BuildStepStatistic(123);
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
        const entity = new BuildStepStatistic();
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
