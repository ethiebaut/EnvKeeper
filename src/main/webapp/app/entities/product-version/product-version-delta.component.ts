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

import { IProductVersion } from 'app/shared/model/product-version.model';
import {DeploymentService} from "../deployment/deployment.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";

@Component({
  selector: 'jhi-product-version-delta',
  templateUrl: './product-version-delta.component.html',
})
export class ProductVersionDeltaComponent implements OnInit {
  productVersions: IProductVersion[] | null = null;
  prettyPrintVersion = prettyPrintVersion;

  constructor(
    protected deploymentService: DeploymentService,
    protected activatedRoute: ActivatedRoute,
    protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productVersions }) => {
      this.productVersions = productVersions;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
