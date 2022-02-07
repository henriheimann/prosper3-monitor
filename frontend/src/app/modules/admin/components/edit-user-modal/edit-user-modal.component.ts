import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UserModel } from '../../../shared/models/user.model';
import { UserService } from '../../../shared/services/user.service';

@Component({
  selector: 'p3m-edit-user-modal',
  templateUrl: './edit-user-modal.component.html',
  styleUrls: ['./edit-user-modal.component.sass']
})
export class EditUserModalComponent implements OnInit {
  user: null | UserModel = null;

  editUserForm = new FormGroup({
    role: new FormControl('USER', Validators.required)
  });

  constructor(private modalRef: BsModalRef, private userService: UserService) {}

  ngOnInit(): void {
    this.editUserForm.controls['role'].setValue(this.user?.role, {
      onlySelf: true
    });
  }

  onCloseClicked(): void {
    this.modalRef.hide();
  }

  onSubmitClicked(): void {
    this.editUserForm.disable();

    this.userService
      .editUser(this.user!.username, {
        role: this.editUserForm.value['role']
      })
      .subscribe({
        next: () => {
          this.modalRef.hide();
        },
        error: () => {
          this.editUserForm.enable();
        }
      });
  }
}
