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

import { TestBed } from '@angular/core/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { LoginModalService } from 'app/core/login/login-modal.service';

// Mock class for NgbModalRef
export class MockNgbModalRef {
  result: Promise<any> = new Promise(resolve => resolve('x'));
}

describe('Service Tests', () => {
  describe('Login Modal Service', () => {
    let service: LoginModalService;
    let modalService: NgbModal;

    beforeEach(() => {
      service = TestBed.get(LoginModalService);
      modalService = TestBed.get(NgbModal);
    });

    describe('Service methods', () => {
      it('Should call open method for NgbModal when open method is called', () => {
        // GIVEN
        const mockModalRef: MockNgbModalRef = new MockNgbModalRef();
        spyOn(modalService, 'open').and.returnValue(mockModalRef);

        // WHEN
        service.open();

        // THEN
        expect(modalService.open).toHaveBeenCalled();
      });

      it('Should call open method for NgbModal one time when open method is called twice', () => {
        // GIVEN
        const mockModalRef: MockNgbModalRef = new MockNgbModalRef();
        spyOn(modalService, 'open').and.returnValue(mockModalRef);

        // WHEN
        service.open();
        service.open();

        // THEN
        expect(modalService.open).toHaveBeenCalledTimes(1);
      });
    });
  });
});
