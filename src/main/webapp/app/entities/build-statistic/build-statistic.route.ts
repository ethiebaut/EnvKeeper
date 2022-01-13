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
import { IBuildStatistic, BuildStatistic } from 'app/shared/model/build-statistic.model';
import { BuildStatisticService } from './build-statistic.service';
import { BuildStatisticComponent } from './build-statistic.component';
import { BuildStatisticDetailComponent } from './build-statistic-detail.component';
import { BuildStatisticUpdateComponent } from './build-statistic-update.component';

@Injectable({ providedIn: 'root' })
export class BuildStatisticResolve implements Resolve<IBuildStatistic> {
  constructor(private service: BuildStatisticService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBuildStatistic> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((buildStatistic: HttpResponse<BuildStatistic>) => {
          if (buildStatistic.body) {
            return of(buildStatistic.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BuildStatistic());
  }
}

export const buildStatisticRoute: Routes = [
  {
    path: '',
    component: BuildStatisticComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,desc',
      pageTitle: 'envKeeperApp.buildStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BuildStatisticDetailComponent,
    resolve: {
      buildStatistic: BuildStatisticResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.buildStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BuildStatisticUpdateComponent,
    resolve: {
      buildStatistic: BuildStatisticResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.buildStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BuildStatisticUpdateComponent,
    resolve: {
      buildStatistic: BuildStatisticResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.buildStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
