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

import {NgModule} from '@angular/core';
import { RouterModule } from '@angular/router';

import { EnvKeeperSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import {MatTabsModule} from '@angular/material/tabs';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {NgsgModule} from "ng-sortgrid";
import {DragDropModule} from "@angular/cdk/drag-drop";
import { MatSnackBarModule } from '@angular/material/snack-bar';

@NgModule({
  imports: [EnvKeeperSharedModule, DragDropModule, MatTabsModule, MatSnackBarModule, NgsgModule, NoopAnimationsModule, RouterModule.forChild(HOME_ROUTE)],
  declarations: [HomeComponent],
})
export class EnvKeeperHomeModule {}
