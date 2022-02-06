import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { UsersListPageComponent } from './components/users-list-page/users-list-page.component';
import { DevicesListPageComponent } from './components/devices-list-page/devices-list-page.component';
import { CreateUserModalComponent } from './components/create-user-modal/create-user-modal.component';
import { EditUserModalComponent } from './components/edit-user-modal/edit-user-modal.component';
import { SharedModule } from "../shared/shared.module";
import { ShowKeysModalComponent } from './components/show-keys-modal/show-keys-modal.component';
import { EditDeviceModalComponent } from './components/edit-device-modal/edit-device-modal.component';
import { CreateDeviceModalComponent } from './components/create-device-modal/create-device-modal.component';


@NgModule({
  declarations: [
    UsersListPageComponent,
    DevicesListPageComponent,
    CreateUserModalComponent,
    EditUserModalComponent,
    ShowKeysModalComponent,
    EditDeviceModalComponent,
    CreateDeviceModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    AdminRoutingModule
  ]
})
export class AdminModule { }
