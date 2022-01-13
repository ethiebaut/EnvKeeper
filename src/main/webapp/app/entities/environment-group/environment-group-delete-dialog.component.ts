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

import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IEnvironmentGroup } from 'app/shared/model/environment-group.model';
import { EnvironmentGroupService } from './environment-group.service';

@Component({
  templateUrl: './environment-group-delete-dialog.component.html',
})
export class EnvironmentGroupDeleteDialogComponent {
  environmentGroup?: IEnvironmentGroup;

  constructor(
    protected environmentGroupService: EnvironmentGroupService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.environmentGroupService.delete(id).subscribe(() => {
      this.eventManager.broadcast('environmentGroupListModification');
      this.activeModal.close();
    });
  }
}
