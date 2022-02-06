import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationComponent } from './components/navigation/navigation.component';
import { RouterModule } from "@angular/router";
import { ReactiveFormsModule } from "@angular/forms";
import { FooterComponent } from './components/footer/footer.component';
import { ConfirmModalComponent } from "./components/confirm-modal/confirm-modal.component";
import { NgSelectModule } from "@ng-select/ng-select";
import { LastContactComponent } from './components/last-contact/last-contact.component';

@NgModule({
    declarations: [
      NavigationComponent,
      FooterComponent,
      ConfirmModalComponent,
      LastContactComponent
    ],
  exports: [
    NavigationComponent,
    ReactiveFormsModule,
    NgSelectModule,
    FooterComponent,
    LastContactComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    NgSelectModule
  ]
})
export class SharedModule { }