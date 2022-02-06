import { Component } from '@angular/core';
import { DeviceService } from "../../../shared/services/device.service";
import { DeviceModel } from "../../../shared/models/device.model";
import { BsModalService } from "ngx-bootstrap/modal";
import { ShowKeysModalComponent } from "../show-keys-modal/show-keys-modal.component";
import { EditDeviceModalComponent } from "../edit-device-modal/edit-device-modal.component";
import { CreateDeviceModalComponent } from "../create-device-modal/create-device-modal.component";
import { ConfirmModalComponent } from "../../../shared/components/confirm-modal/confirm-modal.component";

@Component({
  selector: 'p3m-devices-list-page',
  templateUrl: './devices-list-page.component.html',
  styleUrls: ['./devices-list-page.component.sass']
})
export class DevicesListPageComponent {

  devices$ = this.deviceService.getAll();

  constructor(private deviceService: DeviceService, private modalService: BsModalService) { }

  onCreateDeviceClicked(): void {
    this.modalService.show(CreateDeviceModalComponent, {
      class: 'modal-dialog-centered',
    });
  }

  onShowKeysClicked(device: DeviceModel): void {
    this.modalService.show(ShowKeysModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        deviceId: device.id
      }
    });
  }

  onEditDeviceClicked(device: DeviceModel): void {
    this.modalService.show(EditDeviceModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        device
      }
    });
  }

  onDeleteDeviceClicked(device: DeviceModel): void {
    this.modalService.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        onConfirm: () => {
          this.deviceService.deleteById(device.id).subscribe();
        },
        titleText: 'Sensor löschen',
        bodyText: `Bist du sicher, dass du den Sensor "${device.name}" löschen möchtest?`,
        confirmButtonText: 'Löschen',
        abortButtonText: 'Abbrechen'
      }
    });
  }

  onTtnSyncClicked(): void {
    this.modalService.show(ConfirmModalComponent, {
      class: 'modal-dialog-centered',
      initialState: {
        onConfirm: () => {
          this.deviceService.performDevicesTtnSync().subscribe();
        },
        titleText: 'TTN Synchronisation',
        bodyText: `Bist du sicher, dass du eine Synchronisation mit dem The Things Network durchführen möchtest?`,
        confirmButtonText: 'Synchonisieren',
        abortButtonText: 'Abbrechen'
      }
    });
  }
}
