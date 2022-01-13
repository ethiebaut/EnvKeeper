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
import { BuildComponent } from './build.component';
import { BuildDetailComponent } from './build-detail.component';
import { BuildUpdateComponent } from './build-update.component';
import { BuildDeleteDialogComponent } from './build-delete-dialog.component';
import { buildRoute } from './build.route';

@NgModule({
  imports: [EnvKeeperSharedModule, RouterModule.forChild(buildRoute)],
  declarations: [BuildComponent, BuildDetailComponent, BuildUpdateComponent, BuildDeleteDialogComponent],
  entryComponents: [BuildDeleteDialogComponent],
})
export class EnvKeeperBuildModule {}
