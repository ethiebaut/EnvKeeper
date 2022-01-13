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
import { IBuildStepStatistic, BuildStepStatistic } from 'app/shared/model/build-step-statistic.model';
import { BuildStepStatisticService } from './build-step-statistic.service';
import { BuildStepStatisticComponent } from './build-step-statistic.component';
import { BuildStepStatisticDetailComponent } from './build-step-statistic-detail.component';
import { BuildStepStatisticUpdateComponent } from './build-step-statistic-update.component';

@Injectable({ providedIn: 'root' })
export class BuildStepStatisticResolve implements Resolve<IBuildStepStatistic> {
  constructor(private service: BuildStepStatisticService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBuildStepStatistic> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((buildStepStatistic: HttpResponse<BuildStepStatistic>) => {
          if (buildStepStatistic.body) {
            return of(buildStepStatistic.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BuildStepStatistic());
  }
}

export const buildStepStatisticRoute: Routes = [
  {
    path: '',
    component: BuildStepStatisticComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,desc',
      pageTitle: 'envKeeperApp.buildStepStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BuildStepStatisticDetailComponent,
    resolve: {
      buildStepStatistic: BuildStepStatisticResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.buildStepStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BuildStepStatisticUpdateComponent,
    resolve: {
      buildStepStatistic: BuildStepStatisticResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.buildStepStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BuildStepStatisticUpdateComponent,
    resolve: {
      buildStepStatistic: BuildStepStatisticResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.buildStepStatistic.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
