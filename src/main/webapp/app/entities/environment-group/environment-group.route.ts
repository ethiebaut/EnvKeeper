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
import { IEnvironmentGroup, EnvironmentGroup } from 'app/shared/model/environment-group.model';
import { EnvironmentGroupService } from './environment-group.service';
import { EnvironmentGroupComponent } from './environment-group.component';
import { EnvironmentGroupDetailComponent } from './environment-group-detail.component';
import { EnvironmentGroupUpdateComponent } from './environment-group-update.component';

@Injectable({ providedIn: 'root' })
export class EnvironmentGroupResolve implements Resolve<IEnvironmentGroup> {
  constructor(private service: EnvironmentGroupService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEnvironmentGroup> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((environmentGroup: HttpResponse<EnvironmentGroup>) => {
          if (environmentGroup.body) {
            return of(environmentGroup.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new EnvironmentGroup());
  }
}

export const environmentGroupRoute: Routes = [
  {
    path: '',
    component: EnvironmentGroupComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'shortName,asc',
      pageTitle: 'envKeeperApp.environmentGroup.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnvironmentGroupDetailComponent,
    resolve: {
      environmentGroup: EnvironmentGroupResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.environmentGroup.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnvironmentGroupUpdateComponent,
    resolve: {
      environmentGroup: EnvironmentGroupResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.environmentGroup.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnvironmentGroupUpdateComponent,
    resolve: {
      environmentGroup: EnvironmentGroupResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.environmentGroup.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
