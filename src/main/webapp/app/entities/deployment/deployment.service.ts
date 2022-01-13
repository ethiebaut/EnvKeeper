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
import { IDeployment } from 'app/shared/model/deployment.model';
import { IDeploymentTable } from "app/shared/model/deployment-table.model";

type EntityResponseType = HttpResponse<IDeployment>;
type EntityArrayResponseType = HttpResponse<IDeployment[]>;

@Injectable({ providedIn: 'root' })
export class DeploymentService {
  public resourceUrl = SERVER_API_URL + 'api/deployments';

  constructor(protected http: HttpClient) {}

  create(deployment: IDeployment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(deployment);
    return this.http
      .post<IDeployment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(deployment: IDeployment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(deployment);
    return this.http
      .put<IDeployment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IDeployment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDeployment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  queryTable(req?: any): Observable<HttpResponse<IDeploymentTable[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<IDeploymentTable[]>(this.resourceUrl + '/table', { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  latest(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDeployment[]>(this.resourceUrl + "/latest", { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(deployment: IDeployment): IDeployment {
    const copy: IDeployment = Object.assign({}, deployment, {
      startTime: deployment.startTime && deployment.startTime.isValid() ? deployment.startTime.toJSON() : undefined,
      endTime: deployment.endTime && deployment.endTime.isValid() ? deployment.endTime.toJSON() : undefined,
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
      res.body.forEach((deployment: IDeployment) => {
        deployment.startTime = deployment.startTime ? moment(deployment.startTime) : undefined;
        deployment.endTime = deployment.endTime ? moment(deployment.endTime) : undefined;
      });
    }
    return res;
  }
}
