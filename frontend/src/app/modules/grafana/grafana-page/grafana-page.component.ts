import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'p3m-grafana-page',
  templateUrl: './grafana-page.component.html',
  styleUrls: ['./grafana-page.component.sass']
})
export class GrafanaPageComponent {

  grafanaUrl = this.sanitizer.bypassSecurityTrustResourceUrl(environment.grafanaUrl);

  constructor(private sanitizer: DomSanitizer) {}
}
