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
import { TicketType } from 'app/shared/model/enumerations/ticket-type.model';

export interface ITicket {
  id?: number;
  externalId?: string;
  ticketType?: TicketType;
  summary?: string;
  ticketUrl?: string;
  priority?: string;
  status?: string;
  created?: Moment;
  updated?: Moment;
  reporter?: string;
  assignee?: string;
  description?: any;
  affectsVersion?: string;
  affectsId?: number;
  fixedInVersion?: string;
  fixedInId?: number;
  affectsProductId?: number;
  affectsProductShortname?: string;
  fixedInProductId?: number;
  fixedInProductShortname?: string;
}

export class Ticket implements ITicket {
  constructor(
    public id?: number,
    public externalId?: string,
    public ticketType?: TicketType,
    public summary?: string,
    public ticketUrl?: string,
    public priority?: string,
    public status?: string,
    public created?: Moment,
    public updated?: Moment,
    public reporter?: string,
    public assignee?: string,
    public description?: any,
    public affectsVersion?: string,
    public affectsId?: number,
    public fixedInVersion?: string,
    public fixedInId?: number,
    public affectsProductId?: number,
    public affectsProductShortname?: string,
    public fixedInProductId?: number,
    public fixedInProductShortname?: string
  ) {}
}
