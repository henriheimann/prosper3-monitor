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
    longitude: new FormControl(0.0, Validators.required)
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
        longitude: this.editDeviceForm.value['longitude']
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
