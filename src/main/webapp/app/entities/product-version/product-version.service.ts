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
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IProductVersion } from 'app/shared/model/product-version.model';

type EntityResponseType = HttpResponse<IProductVersion>;
type EntityArrayResponseType = HttpResponse<IProductVersion[]>;

@Injectable({ providedIn: 'root' })
export class ProductVersionService {
  public resourceUrl = SERVER_API_URL + 'api/product-versions';

  constructor(protected http: HttpClient) {}

  create(productVersion: IProductVersion): Observable<EntityResponseType> {
    return this.http.post<IProductVersion>(this.resourceUrl, productVersion, { observe: 'response' });
  }

  update(productVersion: IProductVersion): Observable<EntityResponseType> {
    return this.http.put<IProductVersion>(this.resourceUrl, productVersion, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductVersion>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductVersion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
