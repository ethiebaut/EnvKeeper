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
import { IBuild } from 'app/shared/model/build.model';
import {IDeployment} from "../../shared/model/deployment.model";

type EntityResponseType = HttpResponse<IBuild>;
type EntityArrayResponseType = HttpResponse<IBuild[]>;

@Injectable({ providedIn: 'root' })
export class BuildService {
  public resourceUrl = SERVER_API_URL + 'api/builds';

  constructor(protected http: HttpClient) {}

  create(build: IBuild): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(build);
    return this.http
      .post<IBuild>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(build: IBuild): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(build);
    return this.http
      .put<IBuild>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBuild>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBuild[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(build: IBuild): IBuild {
    const copy: IBuild = Object.assign({}, build, {
      startTime: build.startTime && build.startTime.isValid() ? build.startTime.toJSON() : undefined,
      endTime: build.endTime && build.endTime.isValid() ? build.endTime.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startTime = res.body.startTime ? moment(res.body.startTime) : undefined;
      res.body.endTime = res.body.endTime ? moment(res.body.endTime) : undefined;
      res.body.deployments?.forEach(deployment => {
        this.convertDeploymentDateFromServer(deployment);
      });
    }
    return res;
  }

  protected convertDeploymentDateFromServer(deployment: IDeployment): IDeployment {
    deployment.startTime = deployment.startTime ? moment(deployment.startTime) : undefined;
    deployment.endTime = deployment.endTime ? moment(deployment.endTime) : undefined;
    return deployment;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((build: IBuild) => {
        build.startTime = build.startTime ? moment(build.startTime) : undefined;
        build.endTime = build.endTime ? moment(build.endTime) : undefined;
      });
    }
    return res;
  }
}
