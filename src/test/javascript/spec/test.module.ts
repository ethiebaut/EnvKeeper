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

import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NgModule } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SessionStorageService, LocalStorageService } from 'ngx-webstorage';
import { JhiLanguageService, JhiDataUtils, JhiDateUtils, JhiEventManager, JhiAlertService, JhiParseLinks } from 'ng-jhipster';

import { MockLanguageService } from './helpers/mock-language.service';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { MockLoginModalService } from './helpers/mock-login-modal.service';
import { MockAccountService } from './helpers/mock-account.service';
import { MockActivatedRoute, MockRouter } from './helpers/mock-route.service';
import { MockActiveModal } from './helpers/mock-active-modal.service';
import { MockAlertService } from './helpers/mock-alert.service';
import { MockEventManager } from './helpers/mock-event-manager.service';

@NgModule({
  providers: [
    DatePipe,
    JhiDataUtils,
    JhiDateUtils,
    JhiParseLinks,
    {
      provide: JhiLanguageService,
      useClass: MockLanguageService,
    },
    {
      provide: JhiEventManager,
      useClass: MockEventManager,
    },
    {
      provide: NgbActiveModal,
      useClass: MockActiveModal,
    },
    {
      provide: ActivatedRoute,
      useValue: new MockActivatedRoute({ id: 123 }),
    },
    {
      provide: Router,
      useClass: MockRouter,
    },
    {
      provide: AccountService,
      useClass: MockAccountService,
    },
    {
      provide: LoginModalService,
      useClass: MockLoginModalService,
    },
    {
      provide: JhiAlertService,
      useClass: MockAlertService,
    },
    {
      provide: NgbModal,
      useValue: null,
    },
    {
      provide: SessionStorageService,
      useValue: null,
    },
    {
      provide: LocalStorageService,
      useValue: null,
    },
  ],
  imports: [HttpClientTestingModule],
})
export class EnvKeeperTestModule {}
