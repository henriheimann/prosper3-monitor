import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VisualisationRoutingModule } from './visualisation-routing.module';
import { ClimateMapPageComponent } from './components/climate-map-page/climate-map-page.component';
import { PlantMapPageComponent } from './components/plant-map-page/plant-map-page.component';
import { PlantSensorComponent } from './components/plant-sensor/plant-sensor.component';
import { SensorMapComponent } from './components/sensor-map/sensor-map.component';
import { StoreModule } from '@ngrx/store';
import * as fromVisualisations from './store/visualisation.reducer';
import { EffectsModule } from '@ngrx/effects';
import { VisualisationEffects } from './store/visualisation.effects';
import { SharedModule } from '../shared/shared.module';
import { ClimateSensorComponent } from './components/climate-sensor/climate-sensor.component';
import { UnknownSensorComponent } from './components/unknown-sensor/unknown-sensor.component';
import { SensorDetailComponent } from './components/sensor-detail/sensor-detail.component';
import { StopDraggingModeComponent } from './components/stop-dragging-mode/stop-dragging-mode.component';
import { MeasurementsSelectorComponent } from './components/measurements-selector/measurements-selector.component';
import { MapColorLegendComponent } from './components/map-color-legend/map-color-legend.component';

@NgModule({
  declarations: [
    ClimateMapPageComponent,
    PlantMapPageComponent,
    PlantSensorComponent,
    SensorMapComponent,
    ClimateSensorComponent,
    UnknownSensorComponent,
    SensorDetailComponent,
    StopDraggingModeComponent,
    MeasurementsSelectorComponent,
    MapColorLegendComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    VisualisationRoutingModule,
    StoreModule.forFeature(fromVisualisations.visualisationFeatureKey, fromVisualisations.reducer),
    EffectsModule.forFeature([VisualisationEffects])
  ],
  exports: []
})
export class VisualisationModule {}
