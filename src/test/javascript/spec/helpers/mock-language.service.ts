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

import Spy = jasmine.Spy;
import { JhiLanguageService } from 'ng-jhipster';

import { SpyObject } from './spyobject';

export class MockLanguageService extends SpyObject {
  getCurrentLanguageSpy: Spy;

  constructor() {
    super(JhiLanguageService);

    this.getCurrentLanguageSpy = this.spy('getCurrentLanguage').andReturn('en');
  }
}
