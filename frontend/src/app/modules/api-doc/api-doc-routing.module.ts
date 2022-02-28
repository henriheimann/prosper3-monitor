import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ApiDocPageComponent } from './components/api-doc-page/api-doc-page.component';

const routes: Routes = [
  {
    path: 'api-doc',
    component: ApiDocPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ApiDocRoutingModule {}
