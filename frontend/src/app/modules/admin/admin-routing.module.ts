import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DevicesListPageComponent } from "./components/devices-list-page/devices-list-page.component";
import { UsersListPageComponent } from "./components/users-list-page/users-list-page.component";
import { RoleGuard } from "../auth/guards/role.guard";

const routes: Routes = [
  {
    path: 'admin',
    redirectTo: 'admin/sensors'
  },
  {
    path: 'admin',
    children: [
      {
        path: 'sensors',
        component: DevicesListPageComponent
      },
      {
        path: 'users',
        component: UsersListPageComponent
      }
    ],
    canActivate: [ RoleGuard ],
    data: {
      requiredRoles: ['ADMIN']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
