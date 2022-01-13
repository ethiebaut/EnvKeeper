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
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { ITicket, Ticket } from 'app/shared/model/ticket.model';
import { TicketService } from './ticket.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IProductVersion } from 'app/shared/model/product-version.model';
import { ProductVersionService } from 'app/entities/product-version/product-version.service';

@Component({
  selector: 'jhi-ticket-update',
  templateUrl: './ticket-update.component.html',
})
export class TicketUpdateComponent implements OnInit {
  isSaving = false;
  productversions: IProductVersion[] = [];

  editForm = this.fb.group({
    id: [],
    externalId: [null, [Validators.required]],
    ticketType: [null, [Validators.required]],
    summary: [null, [Validators.required]],
    ticketUrl: [],
    priority: [],
    status: [],
    created: [null, [Validators.required]],
    updated: [null, [Validators.required]],
    reporter: [],
    assignee: [],
    description: [],
    affectsId: [],
    fixedInId: [],
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected ticketService: TicketService,
    protected productVersionService: ProductVersionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticket }) => {
      if (!ticket.id) {
        const today = moment().startOf('day');
        ticket.created = today;
        ticket.updated = today;
      }

      this.updateForm(ticket);

      this.productVersionService.query().subscribe((res: HttpResponse<IProductVersion[]>) => (this.productversions = res.body || []));
    });
  }

  updateForm(ticket: ITicket): void {
    this.editForm.patchValue({
      id: ticket.id,
      externalId: ticket.externalId,
      ticketType: ticket.ticketType,
      summary: ticket.summary,
      ticketUrl: ticket.ticketUrl,
      priority: ticket.priority,
      status: ticket.status,
      created: ticket.created ? ticket.created.format(DATE_TIME_FORMAT) : null,
      updated: ticket.updated ? ticket.updated.format(DATE_TIME_FORMAT) : null,
      reporter: ticket.reporter,
      assignee: ticket.assignee,
      description: ticket.description,
      affectsId: ticket.affectsId,
      fixedInId: ticket.fixedInId,
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: any, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('envKeeperApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ticket = this.createFromForm();
    if (ticket.id !== undefined) {
      this.subscribeToSaveResponse(this.ticketService.update(ticket));
    } else {
      this.subscribeToSaveResponse(this.ticketService.create(ticket));
    }
  }

  private createFromForm(): ITicket {
    return {
      ...new Ticket(),
      id: this.editForm.get(['id'])!.value,
      externalId: this.editForm.get(['externalId'])!.value,
      ticketType: this.editForm.get(['ticketType'])!.value,
      summary: this.editForm.get(['summary'])!.value,
      ticketUrl: this.editForm.get(['ticketUrl'])!.value,
      priority: this.editForm.get(['priority'])!.value,
      status: this.editForm.get(['status'])!.value,
      created: this.editForm.get(['created'])!.value ? moment(this.editForm.get(['created'])!.value, DATE_TIME_FORMAT) : undefined,
      updated: this.editForm.get(['updated'])!.value ? moment(this.editForm.get(['updated'])!.value, DATE_TIME_FORMAT) : undefined,
      reporter: this.editForm.get(['reporter'])!.value,
      assignee: this.editForm.get(['assignee'])!.value,
      description: this.editForm.get(['description'])!.value,
      affectsId: this.editForm.get(['affectsId'])!.value,
      fixedInId: this.editForm.get(['fixedInId'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITicket>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IProductVersion): any {
    return item.id;
  }
}
