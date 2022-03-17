import { Component, OnDestroy, OnInit } from '@angular/core';
import { DeviceService } from '../../../shared/services/device.service';
import { map } from 'rxjs/operators';
import {
  selectDraggingDevice,
  selectIsIdentifyingDevice,
  selectAllDeviceValuesForIndex,
  selectSelectedMeasurementType,
  selectSelectedDevice
} from '../../store/visualisation.selectors';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';
import { combineLatest, Observable, zip } from 'rxjs';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';
import { DeviceModel } from '../../../shared/models/device.model';

@Component({
  selector: 'p3m-climate-map-page',
  templateUrl: './climate-map-page.component.html',
  styleUrls: ['./climate-map-page.component.sass']
})
export class ClimateMapPageComponent implements OnDestroy {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'CLIMATE_SENSOR');
      } else {
        return devices;
      }
    })
  );

  devicesWithValues$: Observable<DeviceWithValuesModel[] | null> = combineLatest([
    this.devices$,
    this.store.select(selectAllDeviceValuesForIndex)
  ]).pipe(
    map(([devices, measurements]) => {
      if (!devices) {
        return null;
      } else {
        return devices.map(
          (device) =>
            ({
              device,
              values: measurements?.deviceValues[device.id]
            } as DeviceWithValuesModel)
        );
      }
    })
  );

  isDraggingDevice$ = this.store.select(selectDraggingDevice).pipe(map((draggingDevice) => draggingDevice != null));
  selectedDevice$: Observable<DeviceModel | null> = this.store.select(selectSelectedDevice);
  draggingDevice$: Observable<DeviceModel | null> = this.store.select(selectDraggingDevice);
  isIdentifyingDevice$ = this.store.select(selectIsIdentifyingDevice);
  selectedMeasurementType$ = this.store.select(selectSelectedMeasurementType);

  constructor(private deviceService: DeviceService, private store: Store) {}

  ngOnDestroy(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }
}
