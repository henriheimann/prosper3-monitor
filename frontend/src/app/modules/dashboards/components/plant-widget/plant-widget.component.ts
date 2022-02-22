import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-plant-widget',
  templateUrl: './plant-widget.component.html',
  styleUrls: ['./plant-widget.component.sass']
})
export class PlantWidgetComponent {
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
