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

import { IProductComponentVersion } from 'app/shared/model/product-component-version.model';
import {ITicket} from "./ticket.model";

export interface IProductVersion {
  id?: number;
  version?: string;
  releaseNotes?: string;
  productShortName?: string;
  productId?: number;
  buildId?: number;
  components?: IProductComponentVersion[];
  fixedTickets?: ITicket[];
}

export class ProductVersion implements IProductVersion {
  constructor(
    public id?: number,
    public version?: string,
    public releaseNotes?: string,
    public productShortName?: string,
    public productId?: number,
    public buildId?: number,
    public components?: IProductComponentVersion[],
    public fixedTickets?: ITicket[]
  ) {}
}
