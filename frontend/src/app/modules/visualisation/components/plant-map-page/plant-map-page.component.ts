import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-plant-map-page',
  templateUrl: './plant-map-page.component.html',
  styleUrls: ['./plant-map-page.component.sass']
})
export class PlantMapPageComponent {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'PLANT_SENSOR');
      } else {
        return devices;
      }
    })
  );

  constructor(private deviceService: DeviceService) {}
}
