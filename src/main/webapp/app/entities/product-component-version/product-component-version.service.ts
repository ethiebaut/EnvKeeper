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
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IProductComponentVersion } from 'app/shared/model/product-component-version.model';

type EntityResponseType = HttpResponse<IProductComponentVersion>;
type EntityArrayResponseType = HttpResponse<IProductComponentVersion[]>;

@Injectable({ providedIn: 'root' })
export class ProductComponentVersionService {
  public resourceUrl = SERVER_API_URL + 'api/product-component-versions';

  constructor(protected http: HttpClient) {}

  create(productComponentVersion: IProductComponentVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(productComponentVersion);
    return this.http
      .post<IProductComponentVersion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(productComponentVersion: IProductComponentVersion): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(productComponentVersion);
    return this.http
      .put<IProductComponentVersion>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IProductComponentVersion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProductComponentVersion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(productComponentVersion: IProductComponentVersion): IProductComponentVersion {
    const copy: IProductComponentVersion = Object.assign({}, productComponentVersion, {
      startTime:
        productComponentVersion.startTime && productComponentVersion.startTime.isValid()
          ? productComponentVersion.startTime.toJSON()
          : undefined,
      endTime:
        productComponentVersion.endTime && productComponentVersion.endTime.isValid() ? productComponentVersion.endTime.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startTime = res.body.startTime ? moment(res.body.startTime) : undefined;
      res.body.endTime = res.body.endTime ? moment(res.body.endTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((productComponentVersion: IProductComponentVersion) => {
        productComponentVersion.startTime = productComponentVersion.startTime ? moment(productComponentVersion.startTime) : undefined;
        productComponentVersion.endTime = productComponentVersion.endTime ? moment(productComponentVersion.endTime) : undefined;
      });
    }
    return res;
  }
}
