import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClimateMonitorRoutingModule } from './climate-monitor-routing.module';
import { ClimateMapPageComponent } from './components/climate-map-page/climate-map-page.component';


@NgModule({
  declarations: [
    ClimateMapPageComponent
  ],
  imports: [
    CommonModule,
    ClimateMonitorRoutingModule
  ]
})
export class ClimateMonitorModule { }
