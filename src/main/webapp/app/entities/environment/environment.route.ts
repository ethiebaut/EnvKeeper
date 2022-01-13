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
import { IEnvironment, Environment } from 'app/shared/model/environment.model';
import { EnvironmentService } from './environment.service';
import { EnvironmentComponent } from './environment.component';
import { EnvironmentDetailComponent } from './environment-detail.component';
import { EnvironmentHistoryComponent } from './environment-history.component';
import { EnvironmentUpdateComponent } from './environment-update.component';

@Injectable({ providedIn: 'root' })
export class EnvironmentResolve implements Resolve<IEnvironment> {
  constructor(private service: EnvironmentService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEnvironment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((environment: HttpResponse<Environment>) => {
          if (environment.body) {
            return of(environment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Environment());
  }
}

export const environmentRoute: Routes = [
  {
    path: '',
    component: EnvironmentComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'envKeeperApp.environment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnvironmentDetailComponent,
    resolve: {
      environment: EnvironmentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.environment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/history',
    redirectTo: ':id/history/list',
    pathMatch: 'full'
  },
  {
    path: ':id/history/list',
    component: EnvironmentHistoryComponent,
    resolve: {
      environment: EnvironmentResolve,
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'startTime,desc',
      pageTitle: 'envKeeperApp.environment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/history/table',
    component: EnvironmentHistoryComponent,
    resolve: {
      environment: EnvironmentResolve,
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'startTime,desc',
      pageTitle: 'envKeeperApp.environment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnvironmentUpdateComponent,
    resolve: {
      environment: EnvironmentResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.environment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnvironmentUpdateComponent,
    resolve: {
      environment: EnvironmentResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.environment.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
