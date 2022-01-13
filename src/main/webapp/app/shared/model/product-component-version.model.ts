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

import { Moment } from 'moment';
import { IProductVersion } from 'app/shared/model/product-version.model';
import { BuildStatus } from 'app/shared/model/enumerations/build-status.model';

export interface IProductComponentVersion {
  id?: number;
  version?: string;
  buildStatus?: BuildStatus;
  startTime?: Moment;
  endTime?: Moment;
  buildUrl?: string;
  releaseNotes?: string;
  componentShortName?: string;
  componentId?: number;
  productVersions?: IProductVersion[];
}

export class ProductComponentVersion implements IProductComponentVersion {
  constructor(
    public id?: number,
    public version?: string,
    public buildStatus?: BuildStatus,
    public startTime?: Moment,
    public endTime?: Moment,
    public buildUrl?: string,
    public releaseNotes?: string,
    public componentShortName?: string,
    public componentId?: number,
    public productVersions?: IProductVersion[]
  ) {}
}
