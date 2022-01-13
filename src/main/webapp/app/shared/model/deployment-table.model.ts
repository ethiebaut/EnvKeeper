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
import { IDeploymentTableCell } from "app/shared/model/deployment-table-cell.model";

export interface IDeploymentTable {
  id?: number;
  startTime?: Moment;
  endTime?: Moment;
  user?: string;
  status?: DeploymentStatus;
  environmentShortName?: string;
  environmentId?: number;
  deploymentTableCells?: IDeploymentTableCell[];
}

export class DeploymentTable implements IDeploymentTable {
  constructor(
    public id?: number,
    public startTime?: Moment,
    public endTime?: Moment,
    public user?: string,
    public status?: DeploymentStatus,
    public environmentShortName?: string,
    public environmentId?: number,
    public deploymentTableCells?: IDeploymentTableCell[]
  ) {}
}
