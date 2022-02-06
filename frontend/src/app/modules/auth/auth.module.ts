import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from "../shared/shared.module";
import { StoreModule } from "@ngrx/store";
import * as fromAuth from './store/auth.reducer';
import { AuthEffects } from "./store/auth.effects";
import { EffectsModule } from "@ngrx/effects";
import { LoginModalComponent } from './components/login-modal/login-modal.component';
import { ChangePasswordModalComponent } from './components/change-password-modal/change-password-modal.component';

@NgModule({
  declarations: [
    LoginModalComponent,
    ChangePasswordModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    StoreModule.forFeature(fromAuth.authFeatureKey, fromAuth.reducer),
    EffectsModule.forFeature([AuthEffects])
  ]
})
export class AuthModule {

}
