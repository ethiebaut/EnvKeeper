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

import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IDeployment, Deployment } from 'app/shared/model/deployment.model';
import { DeploymentService } from './deployment.service';
import { DeploymentComponent } from './deployment.component';
import { DeploymentDetailComponent } from './deployment-detail.component';
import { DeploymentUpdateComponent } from './deployment-update.component';

@Injectable({ providedIn: 'root' })
export class DeploymentResolve implements Resolve<IDeployment> {
  constructor(private service: DeploymentService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDeployment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((deployment: HttpResponse<Deployment>) => {
          if (deployment.body) {
            return of(deployment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Deployment());
  }
}

export const deploymentRoute: Routes = [
  {
    path: '',
    component: DeploymentComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'startTime,desc',
      pageTitle: 'envKeeperApp.deployment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DeploymentDetailComponent,
    resolve: {
      deployment: DeploymentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.deployment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DeploymentUpdateComponent,
    resolve: {
      deployment: DeploymentResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.deployment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DeploymentUpdateComponent,
    resolve: {
      deployment: DeploymentResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.deployment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
