import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DeviceModel } from '../../../shared/models/device.model';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-edit-device-modal',
  templateUrl: './edit-device-modal.component.html',
  styleUrls: ['./edit-device-modal.component.sass']
})
export class EditDeviceModalComponent implements OnInit {
  device: null | DeviceModel = null;

  editDeviceForm = new FormGroup({
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

  ngOnInit(): void {
    this.editDeviceForm.controls['name'].setValue(this.device?.name, {
      onlySelf: true
    });
    this.editDeviceForm.controls['latitude'].setValue(this.device?.latitude, {
      onlySelf: true
    });
    this.editDeviceForm.controls['longitude'].setValue(this.device?.longitude, {
      onlySelf: true
    });
    this.editDeviceForm.controls['qrCodeId'].setValue(this.device?.qrCodeId, {
      onlySelf: true
    });
    this.editDeviceForm.controls['brightnessMin'].setValue(this.device?.brightnessMin, {
      onlySelf: true
    });
    this.editDeviceForm.controls['brightnessMax'].setValue(this.device?.brightnessMax, {
      onlySelf: true
    });
    this.editDeviceForm.controls['moistureCounterMin'].setValue(this.device?.moistureCounterMin, {
      onlySelf: true
    });
    this.editDeviceForm.controls['moistureCounterMax'].setValue(this.device?.moistureCounterMax, {
      onlySelf: true
    });
  }

  onCloseClicked(): void {
    this.modalRef.hide();
  }

  onSubmitClicked(): void {
    this.editDeviceForm.disable();

    this.deviceService
      .editDevice(this.device!.id, {
        name: this.editDeviceForm.value['name'],
        latitude: this.editDeviceForm.value['latitude'],
        longitude: this.editDeviceForm.value['longitude'],
        qrCodeId: this.editDeviceForm.value['qrCodeId'],
        brightnessMin: this.editDeviceForm.value['brightnessMin'],
        brightnessMax: this.editDeviceForm.value['brightnessMax'],
        moistureCounterMin: this.editDeviceForm.value['moistureCounterMin'],
        moistureCounterMax: this.editDeviceForm.value['moistureCounterMax']
      })
      .subscribe({
        next: () => {
          this.modalRef.hide();
        },
        error: () => {
          this.editDeviceForm.enable();
        }
      });
  }
}
