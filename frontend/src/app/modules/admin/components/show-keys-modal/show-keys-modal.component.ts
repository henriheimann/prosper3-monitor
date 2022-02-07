import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { UserModel } from '../../../shared/models/user.model';
import { DeviceModel } from '../../../shared/models/device.model';
import { DeviceService } from '../../../shared/services/device.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'p3m-show-keys-modal',
  templateUrl: './show-keys-modal.component.html',
  styleUrls: ['./show-keys-modal.component.sass']
})
export class ShowKeysModalComponent implements OnInit {
  deviceId: null | number = null;
  device$: null | Observable<DeviceModel | null> = null;

  constructor(private bsModalRef: BsModalRef, private deviceService: DeviceService) {}

  onCloseClicked(): void {
    this.bsModalRef.hide();
  }

  ngOnInit(): void {
    if (this.deviceId) {
      this.device$ = this.deviceService.getById(this.deviceId);
    }
  }
}
