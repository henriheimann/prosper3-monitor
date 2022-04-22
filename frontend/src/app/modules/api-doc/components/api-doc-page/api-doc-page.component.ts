import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'p3m-api-doc-page',
  templateUrl: './api-doc-page.component.html',
  styleUrls: ['./api-doc-page.component.sass']
})
export class ApiDocPageComponent {

  swaggerUiUrl = this.sanitizer.bypassSecurityTrustResourceUrl(environment.backendUrl + "/swagger-ui/");

  constructor(private sanitizer: DomSanitizer) {}
}
