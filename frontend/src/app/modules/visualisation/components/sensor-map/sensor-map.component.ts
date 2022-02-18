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
import { Anchor, GeoJSONSource, PointLike } from 'mapbox-gl';
import { PlantSensorComponent } from '../plant-sensor/plant-sensor.component';
import { environment } from '../../../../../environments/environment';
import { DeviceModel } from '../../../shared/models/device.model';
import { ClimateSensorComponent } from '../climate-sensor/climate-sensor.component';
import { UnknownSensorComponent } from '../unknown-sensor/unknown-sensor.component';
import { Store } from '@ngrx/store';
import {
  selectDraggingDevice,
  SelectedMeasurementsAtSelectedIndex,
  selectSelectedDevice,
  selectSelectedMeasurementsAtSelectedIndex
} from '../../store/visualisation.selectors';
import {
  getMaximumValueForMeasurementType,
  getMinimumValueForMeasurementType
} from '../../models/measurement-type.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'p3m-sensor-map',
  templateUrl: './sensor-map.component.html',
  styleUrls: ['./sensor-map.component.sass']
})
export class SensorMapComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sensorsContainer', { read: ViewContainerRef }) sensorsContainer: ViewContainerRef | undefined;
  sensorRefs: ComponentRef<any>[] = [];

  @Input() showSensorValuesLayer = false;

  @Input() useDeviceLastContact = false;

  map: mapboxgl.Map | undefined;
  mapLoaded: boolean = false;

  sensorValuesFeatureCollection: GeoJSON.FeatureCollection<GeoJSON.Geometry> | undefined;

  selectedDevice$: Observable<DeviceModel | null> = this.store.select(selectSelectedDevice);
  draggingDevice$: Observable<DeviceModel | null> = this.store.select(selectDraggingDevice);

  measurementsAtSelectedIndex$ = this.store.select(selectSelectedMeasurementsAtSelectedIndex);

  devicesToMarkersMap = new Map<DeviceModel, mapboxgl.Marker>();

  constructor(private store: Store) {}

  _devices: DeviceModel[] | null = null;

  @Input() set devices(value: DeviceModel[] | null) {
    this._devices = value;
    this.updateSensors();
  }

  ngOnInit(): void {
    this.map = new mapboxgl.Map({
      container: 'map',
      zoom: 17,
      center: [6.932781, 51.532235],
      style: environment.tileserverStyleUrl,
      attributionControl: false
    });

    this.map.addControl(new mapboxgl.NavigationControl());
    this.map.addControl(
      new mapboxgl.AttributionControl({
        compact: true
      })
    );

    if (this.showSensorValuesLayer) {
      this.measurementsAtSelectedIndex$.subscribe((measurements) => this.onMeasurementsChanged(measurements));
      this.map.on('load', () => this.onMapLoaded());
    }

    this.draggingDevice$.subscribe((draggingDevice) => this.onDraggingDeviceChanged(draggingDevice));
  }

  ngAfterViewInit(): void {
    this.updateSensors();
  }

  onDraggingDeviceChanged(draggingDevice: DeviceModel | null): void {
    this.devicesToMarkersMap.forEach((marker) => marker.setDraggable(false));
    if (draggingDevice != null) {
      const marker = this.devicesToMarkersMap.get(draggingDevice);
      if (marker) {
        marker.setDraggable(true);
      }
    }
  }

  onMeasurementsChanged(measurements: SelectedMeasurementsAtSelectedIndex | null): void {
    if (measurements) {
      const featureCollection: GeoJSON.FeatureCollection<GeoJSON.Geometry> = {
        type: 'FeatureCollection',
        features: []
      };

      for (const deviceId in measurements.sensorValues) {
        const sensorValue = measurements.sensorValues[deviceId];

        const min = getMinimumValueForMeasurementType(measurements.type);
        const max = getMaximumValueForMeasurementType(measurements.type);

        let sensorValuePercentage = 0.0;
        if (sensorValue < min) {
          sensorValuePercentage = 0.0;
        } else if (sensorValue > max) {
          sensorValuePercentage = 100.0;
        } else {
          sensorValuePercentage = (sensorValue - min) / (max - min);
        }

        const device = this._devices?.find((device) => device.id == parseInt(deviceId));
        if (device) {
          featureCollection.features.push({
            type: 'Feature',
            properties: {
              sensorValuePercentage
            },
            geometry: {
              type: 'Point',
              coordinates: [device.longitude, device.latitude]
            }
          });
        }
      }

      this.sensorValuesFeatureCollection = featureCollection;
      this.updateSensorValuesLayer();
    }
  }

  onMapLoaded(): void {
    if (this.map) {
      this.map.addSource('sensorData', {
        type: 'geojson',
        data: {
          type: 'FeatureCollection',
          features: []
        }
      });
      this.map.addLayer({
        id: 'sensorData',
        type: 'circle',
        source: 'sensorData',
        paint: {
          'circle-radius': {
            type: 'exponential',
            stops: [
              [2, 30],
              [6, 300]
            ]
          },
          'circle-opacity': {
            type: 'exponential',
            stops: [
              [-99, 0.0],
              [-10, 0.9]
            ]
          },
          'circle-blur': 1,
          'circle-color': {
            property: 'sensorValuePercentage',
            stops: [
              [0, '#0C2251'],
              [0.2, '#008198'],
              [0.4, '#80B2A9'],
              [0.8, '#F18736'],
              [1, '#E63716']
            ]
          }
        }
      });
      this.mapLoaded = true;
      this.updateSensorValuesLayer();
    }
  }

  updateSensorValuesLayer(): void {
    if (this.map && this.mapLoaded && this.sensorValuesFeatureCollection) {
      (this.map.getSource('sensorData') as GeoJSONSource).setData(this.sensorValuesFeatureCollection);
    }
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
