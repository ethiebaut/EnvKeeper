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

import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { ActivateService } from './activate.service';

@Component({
  selector: 'jhi-confirm',
  templateUrl: './confirm.component.html',
})
export class ConfirmComponent implements OnInit {
  error = false;
  success = false;
  @ViewChild('sitekey', { static: false })
  public sitekeyElement?: ElementRef;

  constructor(
    private activateService: ActivateService,
    private loginModalService: LoginModalService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activateService.sitekey().subscribe(res => {
      (this.sitekeyElement as any).nativeElement.setAttribute('data-sitekey', (res as any).siteKey);
      const dynamicScript = document.createElement('script');
      dynamicScript.type = 'text/javascript';
      dynamicScript.async = true;
      dynamicScript.src = 'https://www.google.com/recaptcha/api.js';
      document.body.appendChild(dynamicScript);
    });
  }

  confirmActivate(): void {
    if (!window['grecaptcha'] || !window['grecaptcha'].getResponse()) {
      this.error = true;
      return;
    }
    this.error = false;
    this.route.queryParams.subscribe(params => {
      this.router.navigate(['/account/activate'], { queryParams: { key: params.key, captcha: window['grecaptcha'].getResponse() } });
    });
  }
}
