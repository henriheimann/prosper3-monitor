import { Component, OnInit } from '@angular/core';
import { DeviceService } from '../../../shared/services/device.service';
import { map } from 'rxjs/operators';
import { selectDraggingDevice } from '../../store/visualisation.selectors';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';

@Component({
  selector: 'p3m-climate-map-page',
  templateUrl: './climate-map-page.component.html',
  styleUrls: ['./climate-map-page.component.sass']
})
export class ClimateMapPageComponent implements OnInit {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'CLIMATE_SENSOR');
      } else {
        return devices;
      }
    })
  );

  isDraggingDevice$ = this.store.select(selectDraggingDevice).pipe(map((draggingDevice) => draggingDevice != null));

  constructor(private deviceService: DeviceService, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }
}
