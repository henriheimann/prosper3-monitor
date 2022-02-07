import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ImprintAndDataProtectionComponent } from './imprint-and-data-protection/imprint-and-data-protection.component';

const routes: Routes = [
  {
    path: 'imprint-and-data-protection',
    component: ImprintAndDataProtectionComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LegalRoutingModule {}
