import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClimateMapPageComponent } from './components/climate-map-page/climate-map-page.component';

const routes: Routes = [
  {
    path: 'climate',
    component: ClimateMapPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClimateMonitorRoutingModule {}
