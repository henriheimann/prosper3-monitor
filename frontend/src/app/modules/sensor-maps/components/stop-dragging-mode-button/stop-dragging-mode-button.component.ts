import { Component, Input } from '@angular/core';
import { stopDraggingSelectedDevice } from '../../store/visualisation.actions';
import { Store } from '@ngrx/store';
import { DeviceModel } from '../../../shared/models/device.model';
import * as mapboxgl from 'mapbox-gl';
import { DeviceService } from '../../../shared/services/device.service';

@Component({
  selector: 'p3m-stop-dragging-mode',
  templateUrl: './stop-dragging-mode-button.component.html',
  styleUrls: ['./stop-dragging-mode-button.component.sass']
})
export class StopDraggingModeButtonComponent {
  @Input() draggingDevice: DeviceModel | null = null;
  @Input() draggingDeviceMarker: mapboxgl.Marker | undefined;

  constructor(private store: Store, private deviceService: DeviceService) {}

  onRevertDraggingClicked(): void {
    if (this.draggingDevice?.longitude && this.draggingDevice.latitude) {
      this.draggingDeviceMarker?.setLngLat([this.draggingDevice.longitude, this.draggingDevice.latitude]);
    }
    this.store.dispatch(stopDraggingSelectedDevice());
  }

  onSaveDraggingClicked(): void {
    if (this.draggingDevice && this.draggingDevice?.id && this.draggingDeviceMarker) {
      this.deviceService
        .editDevice(this.draggingDevice.id, {
          name: this.draggingDevice.name,
          latitude: this.draggingDeviceMarker?.getLngLat().lat,
          longitude: this.draggingDeviceMarker?.getLngLat().lng
        })
        .subscribe();
    }
    this.store.dispatch(stopDraggingSelectedDevice());
  }
}
