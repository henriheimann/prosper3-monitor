import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { deviceIdentified, stopIdentifyingDevice } from '../../store/visualisation.actions';
import { BarcodeFormat, Result } from '@zxing/library';
import { environment } from '../../../../../environments/environment';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-identify-device',
  templateUrl: './identify-device.component.html',
  styleUrls: ['./identify-device.component.sass']
})
export class IdentifyDeviceComponent implements OnInit {
  allowedFormats = [BarcodeFormat.QR_CODE];

  devices$ = this.deviceService.getAll();

  constructor(private store: Store, private deviceService: DeviceService) {}

  ngOnInit(): void {}

  onStopScanningClicked(): void {
    this.store.dispatch(stopIdentifyingDevice());
  }

  onScanCompleted(result: Result) {
    if (result) {
      const resultText = result.getText();
      if (resultText.startsWith(`${environment.deploymentUrl}/devices/`)) {
        const qrCodeId = parseInt(resultText.substr(`${environment.deploymentUrl}/devices/`.length));
        this.devices$.subscribe((devices) => {
          const matchingDevice = devices?.find((device) => device.qrCodeId === qrCodeId);
          if (matchingDevice) {
            this.store.dispatch(
              deviceIdentified({
                device: matchingDevice
              })
            );
          }
        });
      }
    }
  }
}
