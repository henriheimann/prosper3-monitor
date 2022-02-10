import { Component } from '@angular/core';
import { DeviceService } from '../../../shared/services/device.service';
import { filter } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'p3m-climate-map-page',
  templateUrl: './climate-map-page.component.html',
  styleUrls: ['./climate-map-page.component.sass']
})
export class ClimateMapPageComponent {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'CLIMATE_SENSOR');
      } else {
        return devices;
      }
    })
  );

  constructor(private deviceService: DeviceService) {}
}
