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

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { MarkdownModule } from 'ngx-markdown';

import './vendor';
import { EnvKeeperSharedModule } from 'app/shared/shared.module';
import { EnvKeeperCoreModule } from 'app/core/core.module';
import { EnvKeeperAppRoutingModule } from './app-routing.module';
import { EnvKeeperHomeModule } from './home/home.module';
import { EnvKeeperEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
import { EnvGroupComponent } from './env-group/env-group.component';

@NgModule({
  imports: [
    BrowserModule,
    MarkdownModule.forRoot(),
    EnvKeeperSharedModule,
    EnvKeeperCoreModule,
    EnvKeeperHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    EnvKeeperEntityModule,
    EnvKeeperAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent, EnvGroupComponent],
  bootstrap: [MainComponent],
})
export class EnvKeeperAppModule {}
