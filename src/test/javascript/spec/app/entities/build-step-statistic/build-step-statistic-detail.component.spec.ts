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
import { BuildStepStatisticDetailComponent } from 'app/entities/build-step-statistic/build-step-statistic-detail.component';
import { BuildStepStatistic } from 'app/shared/model/build-step-statistic.model';

describe('Component Tests', () => {
  describe('BuildStepStatistic Management Detail Component', () => {
    let comp: BuildStepStatisticDetailComponent;
    let fixture: ComponentFixture<BuildStepStatisticDetailComponent>;
    const route = ({ data: of({ buildStepStatistic: new BuildStepStatistic(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [BuildStepStatisticDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(BuildStepStatisticDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BuildStepStatisticDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load buildStepStatistic on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.buildStepStatistic).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
