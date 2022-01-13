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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDeployment } from 'app/shared/model/deployment.model';
import { prettyPrintIntervalMS } from "app/shared/util/pretty-print-interval";

@Component({
  selector: 'jhi-deployment-detail',
  templateUrl: './deployment-detail.component.html',
})
export class DeploymentDetailComponent implements OnInit {
  deployment: IDeployment | null = null;
  prettyPrintIntervalMS = prettyPrintIntervalMS;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ deployment }) => (this.deployment = deployment));
  }

  previousState(): void {
    window.history.back();
  }
}
