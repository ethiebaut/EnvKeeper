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
import { EnvironmentGroupDetailComponent } from 'app/entities/environment-group/environment-group-detail.component';
import { EnvironmentGroup } from 'app/shared/model/environment-group.model';

describe('Component Tests', () => {
  describe('EnvironmentGroup Management Detail Component', () => {
    let comp: EnvironmentGroupDetailComponent;
    let fixture: ComponentFixture<EnvironmentGroupDetailComponent>;
    const route = ({ data: of({ environmentGroup: new EnvironmentGroup(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EnvKeeperTestModule],
        declarations: [EnvironmentGroupDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(EnvironmentGroupDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(EnvironmentGroupDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load environmentGroup on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.environmentGroup).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
