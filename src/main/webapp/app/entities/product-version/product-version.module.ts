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
import { ProductVersionComponent } from './product-version.component';
import { ProductVersionDetailComponent } from './product-version-detail.component';
import { ProductVersionDeltaComponent } from './product-version-delta.component';
import { ProductVersionUpdateComponent } from './product-version-update.component';
import { ProductVersionDeleteDialogComponent } from './product-version-delete-dialog.component';
import { productVersionRoute } from './product-version.route';
import {MarkdownModule} from "ngx-markdown";

@NgModule({
    imports: [EnvKeeperSharedModule, RouterModule.forChild(productVersionRoute), MarkdownModule],
  declarations: [
    ProductVersionComponent,
    ProductVersionDetailComponent,
    ProductVersionDeltaComponent,
    ProductVersionUpdateComponent,
    ProductVersionDeleteDialogComponent,
  ],
  entryComponents: [ProductVersionDeleteDialogComponent],
})
export class EnvKeeperProductVersionModule {}
