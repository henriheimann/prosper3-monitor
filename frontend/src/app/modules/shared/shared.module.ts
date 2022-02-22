import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationComponent } from './components/navigation/navigation.component';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FooterComponent } from './components/footer/footer.component';
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal.component';
import { LastContactComponent } from './components/last-contact/last-contact.component';
import { NgxSliderModule } from '@angular-slider/ngx-slider';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { TooltipModule } from 'ngx-bootstrap/tooltip';

@NgModule({
  declarations: [NavigationComponent, FooterComponent, ConfirmModalComponent, LastContactComponent],
  exports: [
    NavigationComponent,
    ReactiveFormsModule,
    FooterComponent,
    LastContactComponent,
    NgxSliderModule,
    ZXingScannerModule,
    TooltipModule
  ],
  imports: [CommonModule, RouterModule, ReactiveFormsModule, NgxSliderModule, ZXingScannerModule, TooltipModule]
})
export class SharedModule {}
