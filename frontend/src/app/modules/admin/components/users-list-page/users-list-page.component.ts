import { Component } from '@angular/core';
import { UserService } from "../../../shared/services/user.service";
import { UserModel } from "../../../shared/models/user.model";
import { BsModalService } from "ngx-bootstrap/modal";
import { ConfirmModalComponent } from "../../../shared/components/confirm-modal/confirm-modal.component";
import { EditUserModalComponent } from "../edit-user-modal/edit-user-modal.component";
import { CreateUserModalComponent } from "../create-user-modal/create-user-modal.component";

@Component({
  selector: 'p3m-users-list-page',
  templateUrl: './users-list-page.component.html',
  styleUrls: ['./users-list-page.component.sass']
})
export class UsersListPageComponent {

  users$ = this.userService.getAll();

  constructor(private userService: UserService, private modalService: BsModalService) { }

  onCreateUserClicked() {
    this.modalService.show(CreateUserModalComponent, {
      class: 'modal-dialog-centered',
    });
  }

  onEditUserClicked(user: UserModel) {
    this.modalService.show(EditUserModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        user
      }
    });
  }

  onDeleteUserClicked(user: UserModel): void {
    this.modalService.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        onConfirm: () => {
          this.userService.deleteByUserName(user.username).subscribe();
        },
        titleText: 'Nutzer löschen',
        bodyText: `Bist du sicher, dass du den Nutzer "${user.username}" löschen möchtest?`,
        confirmButtonText: 'Löschen',
        abortButtonText: 'Abbrechen'
      }
    });
  }
}
