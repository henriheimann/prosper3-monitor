import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { logoutUser } from "../../../auth/store/auth.actions";
import { selectIsLoggedIn, selectLoggedInRole, selectLoggedInUsername } from "../../../auth/store/auth.selectors";
import { map } from "rxjs/operators";
import { BsModalService } from "ngx-bootstrap/modal";
import { LoginModalComponent } from "../../../auth/components/login-modal/login-modal.component";
import {
  ChangePasswordModalComponent
} from "../../../auth/components/change-password-modal/change-password-modal.component";

@Component({
  selector: 'p3m-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.sass']
})
export class NavigationComponent {

  isLoggedIn$ = this.store.select(selectIsLoggedIn);

  isUserAdmin$ = this.store.select(selectLoggedInRole).pipe(map(role => {
    return role === 'ADMIN';
  }));

  loggedInUsername$ = this.store.select(selectLoggedInUsername);

  constructor(private router: Router, private store: Store, private modalService: BsModalService) {
  }

  routeStartsWith(prefix: string): boolean {
    return this.router.url.startsWith(prefix);
  }

  onLoginClicked(): void {
    this.modalService.show(LoginModalComponent, { class: 'modal-dialog-centered' });
  }

  onLogoutClicked(): void {
    this.store.dispatch(logoutUser());
  }

  onChangePasswordClicked() {
    this.modalService.show(ChangePasswordModalComponent, { class: 'modal-dialog-centered' });
  }
}
