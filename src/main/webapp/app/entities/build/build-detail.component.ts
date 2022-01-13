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

import { IBuild } from 'app/shared/model/build.model';
import {IBuildStep} from "../../shared/model/build-step.model";
import {BuildStepDeleteDialogComponent} from "../build-step/build-step-delete-dialog.component";
import { BuildStepService } from '../build-step/build-step.service';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpResponse} from "@angular/common/http";
import {IBuildStatistic} from "../../shared/model/build-statistic.model";
import { BuildStatisticService } from '../build-statistic/build-statistic.service';
import {BuildStatisticDeleteDialogComponent} from "../build-statistic/build-statistic-delete-dialog.component";
import {BuildService} from "./build.service";
import {BuildDeleteDialogComponent} from "./build-delete-dialog.component";
import { prettyPrintIntervalMS } from "app/shared/util/pretty-print-interval";
import { prettyPrintVersion } from "app/shared/util/pretty-print-version";
import {IDeployment} from "../../shared/model/deployment.model";
import {DeploymentDeleteDialogComponent} from "../deployment/deployment-delete-dialog.component";

@Component({
  selector: 'jhi-build-detail',
  templateUrl: './build-detail.component.html',
})
export class BuildDetailComponent implements OnInit {
  build: IBuild | null = null;
  childBuilds?: IBuild[] = undefined;
  buildSteps?: IBuildStep[] = undefined;
  buildStatistics?: IBuildStatistic[] = undefined;
  prettyPrintIntervalMS = prettyPrintIntervalMS;
  prettyPrintVersion = prettyPrintVersion;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected buildService: BuildService,
    protected buildStepService: BuildStepService,
    protected buildStatisticService: BuildStatisticService,
    protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ build }) => {
      this.build = build;
      this.childBuilds = undefined;
      this.buildSteps = undefined;
      this.buildStatistics = undefined;
      this.buildService.query({parentBuildId: {equals: this.build?.id}}).subscribe((res: HttpResponse<IBuild[]>) => (
        this.childBuilds = res.body?.sort((a, b) => (a.startTime?.unix() || 0) - (b.startTime?.unix() || 0)) || []));
      this.buildStepService.query({buildId: {equals: this.build?.id}}).subscribe((res: HttpResponse<IBuildStep[]>) => (
        this.buildSteps = res.body?.sort((a, b) => (a.startTime?.unix() || 0) - (b.startTime?.unix() || 0)) || []));
      this.buildStatisticService.query({buildId: {equals: this.build?.id}}).subscribe((res: HttpResponse<IBuildStatistic[]>) => (
        this.buildStatistics = res.body?.sort((a, b) => (a.key || "").localeCompare(b.key || "")) || []));
    });
  }

  previousState(): void {
    window.history.back();
  }

  deleteBuild(build: IBuild): void {
    const modalRef = this.modalService.open(BuildDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.build = build;
  }

  deleteDeployment(deployment: IDeployment): void {
    const modalRef = this.modalService.open(DeploymentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.deployment = deployment;
  }

  deleteBuildStep(buildStep: IBuildStep): void {
    const modalRef = this.modalService.open(BuildStepDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.buildStep = buildStep;
  }

  deleteBuildStatistic(buildStatistic: IBuildStatistic): void {
    const modalRef = this.modalService.open(BuildStatisticDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.buildStatistic = buildStatistic;
  }
}
