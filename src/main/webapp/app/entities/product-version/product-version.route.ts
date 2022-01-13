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
import { IProductVersion, ProductVersion } from 'app/shared/model/product-version.model';
import { ProductVersionService } from './product-version.service';
import { ProductVersionComponent } from './product-version.component';
import { ProductVersionDetailComponent } from './product-version-detail.component';
import { ProductVersionDeltaComponent } from './product-version-delta.component';
import { ProductVersionUpdateComponent } from './product-version-update.component';

@Injectable({ providedIn: 'root' })
export class ProductVersionResolve implements Resolve<IProductVersion> {
  constructor(private service: ProductVersionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductVersion> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((productVersion: HttpResponse<ProductVersion>) => {
          if (productVersion.body) {
            return of(productVersion.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ProductVersion());
  }
}

@Injectable({ providedIn: 'root' })
export class ProductVersionsResolve implements Resolve<IProductVersion[]> {
  constructor(private service: ProductVersionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductVersion[]> | Observable<never> {
    const id = route.params['id'];
    const fromId = route.queryParams['from'];
    const productId = route.queryParams['product'];
    if (id && productId) {
      return this.service.query({id: {greaterThan: fromId || '', lessThanOrEqual: id}, productId: {equals: productId}, sort: ['id,desc'], size: 1000}).pipe(
        flatMap((productVersion: HttpResponse<ProductVersion[]>) => {
          if (productVersion.body) {
            return of(productVersion.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of([]);
  }
}

export const productVersionRoute: Routes = [
  {
    path: '',
    component: ProductVersionComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,desc',
      pageTitle: 'envKeeperApp.productVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductVersionDetailComponent,
    resolve: {
      productVersion: ProductVersionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.productVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/delta',
    component: ProductVersionDeltaComponent,
    resolve: {
      productVersions: ProductVersionsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.productVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductVersionUpdateComponent,
    resolve: {
      productVersion: ProductVersionResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.productVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductVersionUpdateComponent,
    resolve: {
      productVersion: ProductVersionResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.productVersion.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
