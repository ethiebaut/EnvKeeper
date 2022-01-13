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
import { IProductComponent, ProductComponent } from 'app/shared/model/product-component.model';
import { ProductComponentService } from './product-component.service';
import { ProductComponentComponent } from './product-component.component';
import { ProductComponentDetailComponent } from './product-component-detail.component';
import { ProductComponentUpdateComponent } from './product-component-update.component';

@Injectable({ providedIn: 'root' })
export class ProductComponentResolve implements Resolve<IProductComponent> {
  constructor(private service: ProductComponentService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductComponent> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((productComponent: HttpResponse<ProductComponent>) => {
          if (productComponent.body) {
            return of(productComponent.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ProductComponent());
  }
}

export const productComponentRoute: Routes = [
  {
    path: '',
    component: ProductComponentComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,desc',
      pageTitle: 'envKeeperApp.productComponent.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductComponentDetailComponent,
    resolve: {
      productComponent: ProductComponentResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'envKeeperApp.productComponent.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductComponentUpdateComponent,
    resolve: {
      productComponent: ProductComponentResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.productComponent.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductComponentUpdateComponent,
    resolve: {
      productComponent: ProductComponentResolve,
    },
    data: {
      authorities: [Authority.WRITER],
      pageTitle: 'envKeeperApp.productComponent.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
