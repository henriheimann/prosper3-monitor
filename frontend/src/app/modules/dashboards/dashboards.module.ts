import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardsRoutingModule } from './dashboards-routing.module';
import { SharedModule } from '../shared/shared.module';
import { SensorMapsModule } from '../sensor-maps/sensor-maps.module';
import { PageMainDashboardComponent } from './components/page-main-dashboard/page-main-dashboard.component';
import { PageSensorDashboardComponent } from './components/page-sensor-dashboard/page-sensor-dashboard.component';
import { ClimateWidgetComponent } from './components/climate-widget/climate-widget.component';
import { PlantWidgetComponent } from './components/plant-widget/plant-widget.component';
import { ClimateWidgetTabComponent } from './components/climate-widget-tab/climate-widget-tab.component';
import { PlantWidgetTabComponent } from './components/plant-widget-tab/plant-widget-tab.component';

@NgModule({
  declarations: [
    PageMainDashboardComponent,
    PageSensorDashboardComponent,
    ClimateWidgetComponent,
    PlantWidgetComponent,
    ClimateWidgetTabComponent,
    PlantWidgetTabComponent
  ],
  imports: [CommonModule, SharedModule, SensorMapsModule, DashboardsRoutingModule]
})
export class DashboardsModule {}
