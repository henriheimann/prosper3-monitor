import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { BsModalRef } from "ngx-bootstrap/modal";
import { MustMatch } from "../validators/must-match.validator";
import { UserService } from "../../../shared/services/user.service";
import { Store } from "@ngrx/store";
import { selectLoggedInUsername } from "../../store/auth.selectors";
import { map, mergeMap } from "rxjs/operators";

@Component({
  selector: 'p3m-change-password-modal',
  templateUrl: './change-password-modal.component.html',
  styleUrls: ['./change-password-modal.component.sass']
})
export class ChangePasswordModalComponent {

  changePasswordForm = new FormGroup({
      oldPassword: new FormControl('', Validators.required),
      newPassword: new FormControl('', Validators.required),
      newPasswordConfirm: new FormControl('', Validators.required)
    },
    {
      validators: MustMatch('newPassword', 'newPasswordConfirm')
    });

  loggedInUsername$ = this.store.select(selectLoggedInUsername);

  constructor(private modalRef: BsModalRef, private userService: UserService, private store: Store) {
  }

  onCloseClicked() {
    this.modalRef.hide();
  }

  onSubmitClicked() {
    this.changePasswordForm.disable();
    this.loggedInUsername$.pipe(
      map(username => {
        if (username == null) {
          throw new Error("Username not set");
        }
        return username;
      }),
      mergeMap(username => {
        return this.userService.changePassword(username, {
          oldPassword: this.changePasswordForm.value['oldPassword'],
          newPassword: this.changePasswordForm.value['newPassword']
        })
      })
    ).subscribe({
      next: () => {
        this.modalRef.hide();
      },
      error: () => {
        this.changePasswordForm.enable();
      }
    })
  }
}
