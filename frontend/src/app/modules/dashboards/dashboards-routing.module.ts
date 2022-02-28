import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageMainDashboardComponent } from './components/page-main-dashboard/page-main-dashboard.component';
import { PageSensorDashboardComponent } from './components/page-sensor-dashboard/page-sensor-dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: PageMainDashboardComponent,
    data: {
      hideNavigation: true
    }
  },
  {
    path: 'devices/:id',
    component: PageSensorDashboardComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardsRoutingModule {}
