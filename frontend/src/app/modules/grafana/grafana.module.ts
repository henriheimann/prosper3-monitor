import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GrafanaRoutingModule } from './grafana-routing.module';
import { GrafanaPageComponent } from './grafana-page/grafana-page.component';


@NgModule({
  declarations: [
    GrafanaPageComponent
  ],
  imports: [
    CommonModule,
    GrafanaRoutingModule
  ]
})
export class GrafanaModule { }
