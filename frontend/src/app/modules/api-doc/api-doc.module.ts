import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ApiDocRoutingModule } from './api-doc-routing.module';
import { ApiDocPageComponent } from './components/api-doc-page/api-doc-page.component';

@NgModule({
  declarations: [ApiDocPageComponent],
  imports: [CommonModule, ApiDocRoutingModule]
})
export class ApiDocModule {}
