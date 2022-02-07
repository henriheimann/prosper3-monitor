import { Component, OnInit } from '@angular/core';
import { UserModel } from '../../../shared/models/user.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DeviceModel } from '../../../shared/models/device.model';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { UserService } from '../../../shared/services/user.service';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-edit-device-modal',
  templateUrl: './edit-device-modal.component.html',
  styleUrls: ['./edit-device-modal.component.sass']
})
export class EditDeviceModalComponent implements OnInit {
  device: null | DeviceModel = null;

  editDeviceForm = new FormGroup({
    name: new FormControl('', Validators.required)
  });

  constructor(private modalRef: BsModalRef, private deviceService: DeviceService) {}

  ngOnInit(): void {
    this.editDeviceForm.controls['name'].setValue(this.device?.name, {
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
        name: this.editDeviceForm.value['name']
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
