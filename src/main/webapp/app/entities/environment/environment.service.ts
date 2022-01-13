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
import { IEnvironment } from 'app/shared/model/environment.model';

type EntityResponseType = HttpResponse<IEnvironment>;
type EntityArrayResponseType = HttpResponse<IEnvironment[]>;

@Injectable({ providedIn: 'root' })
export class EnvironmentService {
  public resourceUrl = SERVER_API_URL + 'api/environments';

  constructor(protected http: HttpClient) {}

  create(environment: IEnvironment): Observable<EntityResponseType> {
    return this.http.post<IEnvironment>(this.resourceUrl, environment, { observe: 'response' });
  }

  update(environment: IEnvironment): Observable<EntityResponseType> {
    return this.http.put<IEnvironment>(this.resourceUrl, environment, { observe: 'response' });
  }

  bulkPatch(environments: IEnvironment[]): Observable<EntityArrayResponseType> {
    return this.http.patch<IEnvironment[]>(`${this.resourceUrl}/bulk`, environments, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEnvironment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnvironment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
