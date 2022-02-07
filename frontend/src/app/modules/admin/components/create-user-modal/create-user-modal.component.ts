import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MustMatch } from '../../../auth/components/validators/must-match.validator';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { UserService } from '../../../shared/services/user.service';

@Component({
  selector: 'p3m-create-user-modal',
  templateUrl: './create-user-modal.component.html',
  styleUrls: ['./create-user-modal.component.sass']
})
export class CreateUserModalComponent {
  createUserForm = new FormGroup(
    {
      username: new FormControl('', Validators.required),
      role: new FormControl('USER', Validators.required),
      password: new FormControl('', Validators.required),
      passwordConfirm: new FormControl('', Validators.required)
    },
    {
      validators: MustMatch('newPassword', 'newPasswordConfirm')
    }
  );

  constructor(private modalRef: BsModalRef, private userService: UserService) {}

  onCloseClicked() {
    this.modalRef.hide();
  }

  onSubmitClicked() {
    this.createUserForm.disable();

    this.userService
      .createUser({
        username: this.createUserForm.value['username'],
        role: this.createUserForm.value['role'],
        password: this.createUserForm.value['password']
      })
      .subscribe({
        next: () => {
          this.modalRef.hide();
        },
        error: () => {
          this.createUserForm.enable();
        }
      });
  }
}
