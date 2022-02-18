import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { DeviceService } from '../../../shared/services/device.service';
import { selectDevice } from '../../store/visualisation.actions';
import { Store } from '@ngrx/store';

@Component({
  selector: 'p3m-plant-map-page',
  templateUrl: './plant-map-page.component.html',
  styleUrls: ['./plant-map-page.component.sass']
})
export class PlantMapPageComponent implements OnInit {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'PLANT_SENSOR');
      } else {
        return devices;
      }
    })
  );

  constructor(private deviceService: DeviceService, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }
}
