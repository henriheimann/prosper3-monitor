import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-create-device-modal',
  templateUrl: './create-device-modal.component.html',
  styleUrls: ['./create-device-modal.component.sass']
})
export class CreateDeviceModalComponent {
  createDeviceForm = new FormGroup({
    name: new FormControl('', Validators.required)
  });

  constructor(private modalRef: BsModalRef, private deviceService: DeviceService) {}

  onCloseClicked() {
    this.modalRef.hide();
  }

  onSubmitClicked() {
    this.createDeviceForm.disable();

    this.deviceService
      .createDevice({
        name: this.createDeviceForm.value['name']
      })
      .subscribe({
        next: () => {
          this.modalRef.hide();
        },
        error: () => {
          this.createDeviceForm.enable();
        }
      });
  }
}
