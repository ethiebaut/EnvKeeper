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
import { IEnvironmentGroup } from 'app/shared/model/environment-group.model';

type EntityResponseType = HttpResponse<IEnvironmentGroup>;
type EntityArrayResponseType = HttpResponse<IEnvironmentGroup[]>;

@Injectable({ providedIn: 'root' })
export class EnvironmentGroupService {
  public resourceUrl = SERVER_API_URL + 'api/environment-groups';

  constructor(protected http: HttpClient) {}

  create(environmentGroup: IEnvironmentGroup): Observable<EntityResponseType> {
    return this.http.post<IEnvironmentGroup>(this.resourceUrl, environmentGroup, { observe: 'response' });
  }

  update(environmentGroup: IEnvironmentGroup): Observable<EntityResponseType> {
    return this.http.put<IEnvironmentGroup>(this.resourceUrl, environmentGroup, { observe: 'response' });
  }

  bulkPatch(environmentGroups: IEnvironmentGroup[]): Observable<EntityArrayResponseType> {
    return this.http.patch<IEnvironmentGroup[]>(`${this.resourceUrl}/bulk`, environmentGroups, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEnvironmentGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnvironmentGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
