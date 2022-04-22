import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GrafanaPageComponent } from './grafana-page/grafana-page.component';

const routes: Routes = [
  {
    path: 'grafana',
    component: GrafanaPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GrafanaRoutingModule { }
