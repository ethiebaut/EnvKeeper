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
import { EnvironmentComponent } from './environment.component';
import { EnvironmentHistoryComponent } from './environment-history.component';
import { EnvironmentDetailComponent } from './environment-detail.component';
import { EnvironmentUpdateComponent } from './environment-update.component';
import { EnvironmentDeleteDialogComponent } from './environment-delete-dialog.component';
import { environmentRoute } from './environment.route';
import {MatTabsModule} from '@angular/material/tabs';

@NgModule({
  imports: [EnvKeeperSharedModule, MatTabsModule, RouterModule.forChild(environmentRoute)],
  declarations: [EnvironmentComponent, EnvironmentHistoryComponent, EnvironmentDetailComponent, EnvironmentUpdateComponent, EnvironmentDeleteDialogComponent],
  entryComponents: [EnvironmentDeleteDialogComponent],
})
export class EnvKeeperEnvironmentModule {}
