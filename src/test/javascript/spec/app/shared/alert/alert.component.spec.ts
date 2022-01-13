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

import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { JhiAlertService } from 'ng-jhipster';

import { EnvKeeperTestModule } from '../../../test.module';
import { AlertComponent } from 'app/shared/alert/alert.component';

describe('Component Tests', () => {
  describe('Alert Component', () => {
    let comp: AlertComponent;
    let fixture: ComponentFixture<AlertComponent>;
    let alertService: JhiAlertService;

    beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [AlertComponent],
      })
        .overrideTemplate(AlertComponent, '')
        .compileComponents();
    }));

    beforeEach(() => {
      fixture = TestBed.createComponent(AlertComponent);
      comp = fixture.componentInstance;
      alertService = TestBed.get(JhiAlertService);
    });

    it('Should call alertService.get on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(alertService.get).toHaveBeenCalled();
    });

    it('Should call alertService.clear on destroy', () => {
      // WHEN
      comp.ngOnDestroy();

      // THEN
      expect(alertService.clear).toHaveBeenCalled();
    });
  });
});
