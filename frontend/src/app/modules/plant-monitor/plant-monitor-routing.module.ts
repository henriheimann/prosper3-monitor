import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlantMapPageComponent } from './components/plant-map-page/plant-map-page.component';

const routes: Routes = [
  {
    path: 'plants',
    component: PlantMapPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlantMonitorRoutingModule {}
