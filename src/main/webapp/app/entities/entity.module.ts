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

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'environment-group',
        loadChildren: () => import('./environment-group/environment-group.module').then(m => m.EnvKeeperEnvironmentGroupModule),
      },
      {
        path: 'environment',
        loadChildren: () => import('./environment/environment.module').then(m => m.EnvKeeperEnvironmentModule),
      },
      {
        path: 'product',
        loadChildren: () => import('./product/product.module').then(m => m.EnvKeeperProductModule),
      },
      {
        path: 'product-version',
        loadChildren: () => import('./product-version/product-version.module').then(m => m.EnvKeeperProductVersionModule),
      },
      {
        path: 'deployment',
        loadChildren: () => import('./deployment/deployment.module').then(m => m.EnvKeeperDeploymentModule),
      },
      {
        path: 'product-component',
        loadChildren: () => import('./product-component/product-component.module').then(m => m.EnvKeeperProductComponentModule),
      },
      {
        path: 'product-component-version',
        loadChildren: () =>
          import('./product-component-version/product-component-version.module').then(m => m.EnvKeeperProductComponentVersionModule),
      },
      {
        path: 'build',
        loadChildren: () => import('./build/build.module').then(m => m.EnvKeeperBuildModule),
      },
      {
        path: 'build-statistic',
        loadChildren: () => import('./build-statistic/build-statistic.module').then(m => m.EnvKeeperBuildStatisticModule),
      },
      {
        path: 'build-step',
        loadChildren: () => import('./build-step/build-step.module').then(m => m.EnvKeeperBuildStepModule),
      },
      {
        path: 'build-step-statistic',
        loadChildren: () => import('./build-step-statistic/build-step-statistic.module').then(m => m.EnvKeeperBuildStepStatisticModule),
      },
      {
        path: 'ticket',
        loadChildren: () => import('./ticket/ticket.module').then(m => m.EnvKeeperTicketModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EnvKeeperEntityModule {}
