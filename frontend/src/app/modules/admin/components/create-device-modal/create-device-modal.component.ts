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
    name: new FormControl('', Validators.required),
    latitude: new FormControl(0.0, Validators.required),
    longitude: new FormControl(0.0, Validators.required),
    qrCodeId: new FormControl(null),
    brightnessMin: new FormControl(null),
    brightnessMax: new FormControl(null),
    moistureCounterMin: new FormControl(null),
    moistureCounterMax: new FormControl(null)
  });

  constructor(private modalRef: BsModalRef, private deviceService: DeviceService) {}

  onCloseClicked() {
    this.modalRef.hide();
  }

  onSubmitClicked() {
    this.createDeviceForm.disable();

    this.deviceService
      .createDevice({
        name: this.createDeviceForm.value['name'],
        latitude: this.createDeviceForm.value['latitude'],
        longitude: this.createDeviceForm.value['longitude'],
        qrCodeId: this.createDeviceForm.value['qrCodeId'],
        brightnessMin: this.createDeviceForm.value['brightnessMin'],
        brightnessMax: this.createDeviceForm.value['brightnessMax'],
        moistureCounterMin: this.createDeviceForm.value['moistureCounterMin'],
        moistureCounterMax: this.createDeviceForm.value['moistureCounterMax']
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
