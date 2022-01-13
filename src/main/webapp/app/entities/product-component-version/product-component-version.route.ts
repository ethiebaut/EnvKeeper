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
import { IProductComponentVersion, ProductComponentVersion } from 'app/shared/model/product-component-version.model';
import { ProductComponentVersionService } from './product-component-version.service';
import { ProductComponentVersionComponent } from './product-component-version.component';
import { ProductComponentVersionDetailComponent } from './product-component-version-detail.component';
import { ProductComponentVersionUpdateComponent } from './product-component-version-update.component';

@Injectable({ providedIn: 'root' })
export class ProductComponentVersionResolve implements Resolve<IProductComponentVersion> {
  constructor(private service: ProductComponentVersionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductComponentVersion> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((productComponentVersion: HttpResponse<ProductComponentVersion>) => {
          if (productComponentVersion.body) {
            return of(productComponentVersion.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ProductComponentVersion());
  }
}

export const productComponentVersionRoute: Routes = [
  {
    path: '',
    component: ProductComponentVersionComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'envKeeperApp.productComponentVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductComponentVersionDetailComponent,
    resolve: {
      productComponentVersion: ProductComponentVersionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.productComponentVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductComponentVersionUpdateComponent,
    resolve: {
      productComponentVersion: ProductComponentVersionResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.productComponentVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductComponentVersionUpdateComponent,
    resolve: {
      productComponentVersion: ProductComponentVersionResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.productComponentVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
