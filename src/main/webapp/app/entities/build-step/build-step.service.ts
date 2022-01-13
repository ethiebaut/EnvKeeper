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
import { IBuildStep } from 'app/shared/model/build-step.model';

type EntityResponseType = HttpResponse<IBuildStep>;
type EntityArrayResponseType = HttpResponse<IBuildStep[]>;

@Injectable({ providedIn: 'root' })
export class BuildStepService {
  public resourceUrl = SERVER_API_URL + 'api/build-steps';

  constructor(protected http: HttpClient) {}

  create(buildStep: IBuildStep): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(buildStep);
    return this.http
      .post<IBuildStep>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(buildStep: IBuildStep): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(buildStep);
    return this.http
      .put<IBuildStep>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBuildStep>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBuildStep[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(buildStep: IBuildStep): IBuildStep {
    const copy: IBuildStep = Object.assign({}, buildStep, {
      startTime: buildStep.startTime && buildStep.startTime.isValid() ? buildStep.startTime.toJSON() : undefined,
      endTime: buildStep.endTime && buildStep.endTime.isValid() ? buildStep.endTime.toJSON() : undefined,
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
      res.body.forEach((buildStep: IBuildStep) => {
        buildStep.startTime = buildStep.startTime ? moment(buildStep.startTime) : undefined;
        buildStep.endTime = buildStep.endTime ? moment(buildStep.endTime) : undefined;
      });
    }
    return res;
  }
}
