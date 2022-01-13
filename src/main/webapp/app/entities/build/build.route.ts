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
import { IBuild, Build } from 'app/shared/model/build.model';
import { BuildService } from './build.service';
import { BuildComponent } from './build.component';
import { BuildDetailComponent } from './build-detail.component';
import { BuildUpdateComponent } from './build-update.component';

@Injectable({ providedIn: 'root' })
export class BuildResolve implements Resolve<IBuild> {
  constructor(private service: BuildService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBuild> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((build: HttpResponse<Build>) => {
          if (build.body) {
            return of(build.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Build());
  }
}

export const buildRoute: Routes = [
  {
    path: '',
    component: BuildComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'startTime,desc',
      pageTitle: 'envKeeperApp.build.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BuildDetailComponent,
    resolve: {
      build: BuildResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.build.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BuildUpdateComponent,
    resolve: {
      build: BuildResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.build.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BuildUpdateComponent,
    resolve: {
      build: BuildResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.build.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
