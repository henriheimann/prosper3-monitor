import {
  AfterViewInit,
  Component,
  ComponentRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import * as mapboxgl from 'mapbox-gl';
import { PlantSensorComponent } from '../plant-sensor/plant-sensor.component';
import { environment } from '../../../../../environments/environment';
import { DeviceModel } from '../../../shared/models/device.model';
import { ClimateSensorComponent } from '../climate-sensor/climate-sensor.component';
import { UnknownSensorComponent } from '../unknown-sensor/unknown-sensor.component';
import { Anchor, PointLike } from 'mapbox-gl';
import { Store } from '@ngrx/store';
import { selectDraggingDevice, selectSelectedDevice } from '../../store/visualisation.selectors';
import { selectDevice } from '../../store/visualisation.actions';

@Component({
  selector: 'p3m-sensor-map',
  templateUrl: './sensor-map.component.html',
  styleUrls: ['./sensor-map.component.sass']
})
export class SensorMapComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sensorsContainer', { read: ViewContainerRef }) sensorsContainer: ViewContainerRef | undefined;
  sensorRefs: ComponentRef<any>[] = [];

  map: mapboxgl.Map | undefined;

  _devices: DeviceModel[] | null = null;

  selectedDevice$ = this.store.select(selectSelectedDevice);
  draggingDevice$ = this.store.select(selectDraggingDevice);

  devicesToMarkersMap = new Map<DeviceModel, mapboxgl.Marker>();

  @Input() set devices(value: DeviceModel[] | null) {
    this._devices = value;
    this.updateSensors();
  }

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.map = new mapboxgl.Map({
      container: 'map',
      zoom: 17,
      center: [6.932781, 51.532235],
      style: environment.tileserverStyleUrl
    });

    this?.map.addControl(new mapboxgl.NavigationControl());

    this.draggingDevice$.subscribe((draggingDevice) => {
      this.devicesToMarkersMap.forEach((marker) => marker.setDraggable(false));
      if (draggingDevice != null) {
        const marker = this.devicesToMarkersMap.get(draggingDevice);
        if (marker) {
          marker.setDraggable(true);
        }
      }
    });
  }

  ngAfterViewInit(): void {
    this.updateSensors();
  }

  updateSensors(): void {
    if (this.map && this.sensorsContainer) {
      this.sensorsContainer.clear();
      this.sensorRefs = [];
      this.devicesToMarkersMap.clear();
      if (this._devices) {
        for (let device of this._devices) {
          let componentRef = null;
          let anchor: Anchor = 'center';
          let offset: PointLike = [0, 0];

          if (device?.lastContact?.sensorType == 'PLANT_SENSOR') {
            componentRef = this.sensorsContainer.createComponent(PlantSensorComponent);
            componentRef.instance.device = device;
            anchor = 'bottom';
            offset = [0, 12.11];
          } else if (device?.lastContact?.sensorType == 'CLIMATE_SENSOR') {
            componentRef = this.sensorsContainer.createComponent(ClimateSensorComponent);
            componentRef.instance.device = device;
          } else {
            componentRef = this.sensorsContainer.createComponent(UnknownSensorComponent);
          }

          const marker = new mapboxgl.Marker(componentRef.location.nativeElement, {
            anchor,
            offset,
            draggable: false
          })
            .setLngLat([device.longitude, device.latitude])
            .addTo(this.map);
          this.devicesToMarkersMap.set(device, marker);

          this.sensorRefs.push(componentRef);
        }
      }
    }
  }

  ngOnDestroy(): void {
    this.sensorRefs.forEach((sensorRef) => sensorRef.destroy());
  }
}
