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
import { BuildStepStatisticComponent } from './build-step-statistic.component';
import { BuildStepStatisticDetailComponent } from './build-step-statistic-detail.component';
import { BuildStepStatisticUpdateComponent } from './build-step-statistic-update.component';
import { BuildStepStatisticDeleteDialogComponent } from './build-step-statistic-delete-dialog.component';
import { buildStepStatisticRoute } from './build-step-statistic.route';

@NgModule({
  imports: [EnvKeeperSharedModule, RouterModule.forChild(buildStepStatisticRoute)],
  declarations: [
    BuildStepStatisticComponent,
    BuildStepStatisticDetailComponent,
    BuildStepStatisticUpdateComponent,
    BuildStepStatisticDeleteDialogComponent,
  ],
  entryComponents: [BuildStepStatisticDeleteDialogComponent],
})
export class EnvKeeperBuildStepStatisticModule {}
