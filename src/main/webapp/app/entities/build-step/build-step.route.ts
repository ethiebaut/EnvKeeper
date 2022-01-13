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
import { IBuildStep, BuildStep } from 'app/shared/model/build-step.model';
import { BuildStepService } from './build-step.service';
import { BuildStepComponent } from './build-step.component';
import { BuildStepDetailComponent } from './build-step-detail.component';
import { BuildStepUpdateComponent } from './build-step-update.component';

@Injectable({ providedIn: 'root' })
export class BuildStepResolve implements Resolve<IBuildStep> {
  constructor(private service: BuildStepService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBuildStep> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((buildStep: HttpResponse<BuildStep>) => {
          if (buildStep.body) {
            return of(buildStep.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BuildStep());
  }
}

export const buildStepRoute: Routes = [
  {
    path: '',
    component: BuildStepComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'startTime,desc',
      pageTitle: 'envKeeperApp.buildStep.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BuildStepDetailComponent,
    resolve: {
      buildStep: BuildStepResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.buildStep.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BuildStepUpdateComponent,
    resolve: {
      buildStep: BuildStepResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.buildStep.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BuildStepUpdateComponent,
    resolve: {
      buildStep: BuildStepResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.buildStep.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
