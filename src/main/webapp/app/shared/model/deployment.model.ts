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
import { DeploymentStatus } from 'app/shared/model/enumerations/deployment-status.model';
import {Product} from "app/shared/model/product.model";

export interface IDeployment {
  id?: number;
  startTime?: Moment;
  endTime?: Moment;
  user?: string;
  status?: DeploymentStatus;
  namespace?: string;
  url?: string;
  testUrl?: string;
  productVersionVersion?: string;
  productVersionId?: number;
  environmentShortName?: string;
  environmentId?: number;
  buildId?: number;
  product?: Product;
  deployment?: Deployment;
}

export class Deployment implements IDeployment {
  constructor(
    public id?: number,
    public startTime?: Moment,
    public endTime?: Moment,
    public user?: string,
    public status?: DeploymentStatus,
    public namespace?: string,
    public url?: string,
    public testUrl?: string,
    public productVersionVersion?: string,
    public productVersionId?: number,
    public environmentShortName?: string,
    public environmentId?: number,
    public buildId?: number,
    public product?: Product,
    public deployment?: Deployment
  ) {}
}
