import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectAllMeasurementsAtSelectedIndexForSelectedDevice,
  selectSelectedDevice
} from '../../store/visualisation.selectors';
import { selectDevice, startDraggingSelectedDevice } from '../../store/visualisation.actions';
import { Observable } from 'rxjs';
import { DeviceModel } from '../../../shared/models/device.model';
import { SensorValuesModel } from '../../../shared/models/sensor-values.model';

@Component({
  selector: 'p3m-sensor-detail',
  templateUrl: './sensor-detail.component.html',
  styleUrls: ['./sensor-detail.component.sass']
})
export class SensorDetailComponent {
  selectedDevice$: Observable<DeviceModel | null> = this.store.select(selectSelectedDevice);

  measurements$: Observable<{ timestamp: string; sensorValues: SensorValuesModel } | null> = this.store.select(
    selectAllMeasurementsAtSelectedIndexForSelectedDevice
  );

  @Input() useDeviceLastContact = false;

  constructor(private store: Store) {}

  onCloseClicked(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }

  onMoveClicked(): void {
    this.store.dispatch(startDraggingSelectedDevice());
  }

  onEditClicked(): void {}

  formatDate(timestamp: string): string {
    return new Date(timestamp).toLocaleString([], {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
