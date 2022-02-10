import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VisualisationsRoutingModule } from './visualisations-routing.module';
import { ClimateMapPageComponent } from './components/climate-map-page/climate-map-page.component';
import { PlantMapPageComponent } from './components/plant-map-page/plant-map-page.component';
import { PlantSensorComponent } from './components/plant-sensor/plant-sensor.component';
import { SensorMapComponent } from './components/sensor-map/sensor-map.component';

@NgModule({
  declarations: [ClimateMapPageComponent, PlantMapPageComponent, PlantSensorComponent, SensorMapComponent],
  imports: [CommonModule, VisualisationsRoutingModule],
  exports: []
})
export class VisualisationsModule {}
