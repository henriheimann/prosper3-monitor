import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlantMonitorRoutingModule } from './plant-monitor-routing.module';
import { PlantMapPageComponent } from './components/plant-map-page/plant-map-page.component';

@NgModule({
  declarations: [PlantMapPageComponent],
  imports: [CommonModule, PlantMonitorRoutingModule]
})
export class PlantMonitorModule {}
