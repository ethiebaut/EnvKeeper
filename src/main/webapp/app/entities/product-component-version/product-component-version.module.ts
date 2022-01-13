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
import { RouterModule } from '@angular/router';

import { EnvKeeperSharedModule } from 'app/shared/shared.module';
import { ProductComponentVersionComponent } from './product-component-version.component';
import { ProductComponentVersionDetailComponent } from './product-component-version-detail.component';
import { ProductComponentVersionUpdateComponent } from './product-component-version-update.component';
import { ProductComponentVersionDeleteDialogComponent } from './product-component-version-delete-dialog.component';
import { productComponentVersionRoute } from './product-component-version.route';
import {MarkdownModule} from "ngx-markdown";

@NgModule({
  imports: [EnvKeeperSharedModule, RouterModule.forChild(productComponentVersionRoute), MarkdownModule],
  declarations: [
    ProductComponentVersionComponent,
    ProductComponentVersionDetailComponent,
    ProductComponentVersionUpdateComponent,
    ProductComponentVersionDeleteDialogComponent,
  ],
  entryComponents: [ProductComponentVersionDeleteDialogComponent],
})
export class EnvKeeperProductComponentVersionModule {}
