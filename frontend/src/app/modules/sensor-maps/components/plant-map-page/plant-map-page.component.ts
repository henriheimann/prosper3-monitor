import { Component, OnDestroy, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { DeviceService } from '../../../shared/services/device.service';
import { selectDevice } from '../../store/visualisation.actions';
import { Store } from '@ngrx/store';
import {
  selectDraggingDevice,
  selectIsIdentifyingDevice,
  selectSelectedDevice
} from '../../store/visualisation.selectors';
import { Observable, zip } from 'rxjs';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';
import { DeviceModel } from '../../../shared/models/device.model';

@Component({
  selector: 'p3m-plant-map-page',
  templateUrl: './plant-map-page.component.html',
  styleUrls: ['./plant-map-page.component.sass']
})
export class PlantMapPageComponent implements OnDestroy {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'PLANT_SENSOR');
      } else {
        return devices;
      }
    })
  );

  devicesWithValues$: Observable<DeviceWithValuesModel[] | null> = this.devices$.pipe(
    map((devices) => {
      if (!devices) {
        return null;
      } else {
        return devices?.map(
          (device) =>
            ({
              device,
              values: device.lastContact?.deviceValues
            } as DeviceWithValuesModel)
        );
      }
    })
  );

  isIdentifyingDevice$ = this.store.select(selectIsIdentifyingDevice);
  selectedDevice$: Observable<DeviceModel | null> = this.store.select(selectSelectedDevice);
  draggingDevice$: Observable<DeviceModel | null> = this.store.select(selectDraggingDevice);

  constructor(private deviceService: DeviceService, private store: Store) {}

  ngOnDestroy(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }
}
