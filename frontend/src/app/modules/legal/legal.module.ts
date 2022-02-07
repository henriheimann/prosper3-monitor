import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LegalRoutingModule } from './legal-routing.module';
import { ImprintAndDataProtectionComponent } from './imprint-and-data-protection/imprint-and-data-protection.component';

@NgModule({
  declarations: [ImprintAndDataProtectionComponent],
  imports: [CommonModule, LegalRoutingModule]
})
export class LegalModule {}
