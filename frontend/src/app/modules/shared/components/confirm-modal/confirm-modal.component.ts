import { Component } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';

@Component({
  selector: 'p3m-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.scss']
})
export class ConfirmModalComponent {
  titleText = 'Confirm operation';
  bodyText = 'Are you sure to want to perform this operation?';
  confirmButtonText = 'Confirm';
  abortButtonText = 'Abort';
  onConfirm: (() => any) | undefined;
  onAbort: (() => any) | undefined;

  constructor(private modalRef: BsModalRef) {}

  onCloseClicked(): void {
    if (this.onAbort) {
      this.onAbort();
    }
    this.modalRef.hide();
  }

  onConfirmClicked(): void {
    if (this.onConfirm) {
      this.onConfirm();
    }
    this.modalRef.hide();
  }

  onAbortClicked(): void {
    if (this.onAbort) {
      this.onAbort();
    }
    this.modalRef.hide();
  }
}
