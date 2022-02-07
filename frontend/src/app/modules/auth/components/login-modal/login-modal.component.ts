import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Store } from '@ngrx/store';
import { selectIsLoggedIn, selectLoggingIn } from '../../store/auth.selectors';
import { loginUser } from '../../store/auth.actions';

@Component({
  selector: 'p3m-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.sass']
})
export class LoginModalComponent {
  loginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required)
  });

  loggingIn$ = this.store.select(selectLoggingIn);
  isLoggedIn$ = this.store.select(selectIsLoggedIn);

  constructor(public modalRef: BsModalRef, private store: Store) {
    this.loggingIn$.subscribe((loggingIn) => {
      if (loggingIn) {
        this.loginForm.disable();
      } else {
        this.loginForm.enable();
      }
    });
    this.isLoggedIn$.subscribe((isLoggedIn) => {
      if (isLoggedIn) {
        this.modalRef.hide();
      }
    });
  }

  onCloseClicked() {
    this.modalRef.hide();
  }

  onSubmitClicked() {
    this.store.dispatch(loginUser(this.loginForm.value));
  }
}
