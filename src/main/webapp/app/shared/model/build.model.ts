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
import { IDeployment } from 'app/shared/model/deployment.model';
import { IProductVersion } from 'app/shared/model/product-version.model';
import { BuildStatus } from 'app/shared/model/enumerations/build-status.model';

export interface IBuild {
  id?: number;
  buildUrl?: string;
  buildName?: string;
  status?: BuildStatus;
  startTime?: Moment;
  endTime?: Moment;
  builds?: IBuild[];
  parentBuildId?: number;
  deployments?: IDeployment[];
  productVersions?: IProductVersion[];
}

export class Build implements IBuild {
  constructor(
    public id?: number,
    public buildUrl?: string,
    public buildName?: string,
    public status?: BuildStatus,
    public startTime?: Moment,
    public endTime?: Moment,
    public builds?: IBuild[],
    public parentBuildId?: number,
    public deployments?: IDeployment[],
    public productVersions?: IProductVersion[]
  ) {}
}
