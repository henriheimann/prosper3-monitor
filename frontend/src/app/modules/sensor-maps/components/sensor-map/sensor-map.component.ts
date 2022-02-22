import { AfterViewInit, Component, ComponentRef, Input, OnDestroy, ViewChild, ViewContainerRef } from '@angular/core';
import * as mapboxgl from 'mapbox-gl';
import { Anchor, GeoJSONSource, PointLike } from 'mapbox-gl';
import { PlantSensorComponent } from '../plant-sensor/plant-sensor.component';
import { environment } from '../../../../../environments/environment';
import { DeviceModel } from '../../../shared/models/device.model';
import { ClimateSensorComponent } from '../climate-sensor/climate-sensor.component';
import { UnknownSensorComponent } from '../unknown-sensor/unknown-sensor.component';
import { Store } from '@ngrx/store';
import { selectDraggingDevice, selectSelectedDevice } from '../../store/visualisation.selectors';
import { Observable } from 'rxjs';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';
import {
  getMaximumValueForMeasurementType,
  getMinimumValueForMeasurementType,
  MeasurementTypeModel
} from '../../models/measurement-type.model';
import { extractValueOfType } from '../../../shared/models/device-values.model';

@Component({
  selector: 'p3m-sensor-map',
  templateUrl: './sensor-map.component.html',
  styleUrls: ['./sensor-map.component.sass']
})
export class SensorMapComponent implements AfterViewInit, OnDestroy {
  @ViewChild('sensorsContainer', { read: ViewContainerRef }) sensorsContainer: ViewContainerRef | undefined;
  sensorRefs: ComponentRef<any>[] = [];

  @Input() showSensorValuesLayer = false;
  @Input() allowSensorSelection = true;
  @Input() useDeviceLastContact = false;
  @Input() selectedMeasurementType: MeasurementTypeModel | null = null;

  selectedDevice$: Observable<DeviceModel | null> = this.store.select(selectSelectedDevice);
  draggingDevice$: Observable<DeviceModel | null> = this.store.select(selectDraggingDevice);

  mapId = Math.random().toString(36).substring(2);
  map: mapboxgl.Map | undefined;
  mapLoaded: boolean = false;

  devicesToMarkersMap = new Map<DeviceModel, mapboxgl.Marker>();

  constructor(private store: Store) {}

  _devicesWithValues: DeviceWithValuesModel[] | null = null;

  @Input() set devicesWithValues(value: DeviceWithValuesModel[] | null) {
    this._devicesWithValues = value;
    this.onDeviceWithValuesChanged();
  }

  ngAfterViewInit(): void {
    this.map = new mapboxgl.Map({
      container: this.mapId,
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

    this.map.on('load', () => this.onMapLoaded());
    this.draggingDevice$.subscribe((draggingDevice) => this.onDraggingDeviceChanged(draggingDevice));

    this.onDeviceWithValuesChanged();
  }

  ngOnDestroy(): void {
    this.sensorRefs.forEach((sensorRef) => sensorRef.destroy());
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

  onMapLoaded(): void {
    if (!this.map) {
      return;
    }
    this.mapLoaded = true;
    this.createSensorDataLayer();
    this.updateSensorDataLayer();
  }

  onDeviceWithValuesChanged(): void {
    this.updateDeviceMarkers();
    this.updateSensorDataLayer();
  }

  createSensorDataLayer(): void {
    if (!this.map) {
      return;
    }

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
  }

  updateSensorDataLayer(): void {
    if (this._devicesWithValues && this.selectedMeasurementType) {
      const featureCollection: GeoJSON.FeatureCollection<GeoJSON.Geometry> = {
        type: 'FeatureCollection',
        features: []
      };

      for (const deviceWithValues of this._devicesWithValues) {
        const deviceValues = deviceWithValues.values;
        if (deviceValues == null) {
          continue;
        }

        const value = extractValueOfType(deviceValues, this.selectedMeasurementType);

        if (value == null) {
          continue;
        }

        const min = getMinimumValueForMeasurementType(this.selectedMeasurementType);
        const max = getMaximumValueForMeasurementType(this.selectedMeasurementType);

        let valuePercentage = 0.0;
        if (value < min) {
          valuePercentage = 0.0;
        } else if (value > max) {
          valuePercentage = 100.0;
        } else {
          valuePercentage = (value - min) / (max - min);
        }

        featureCollection.features.push({
          type: 'Feature',
          properties: {
            sensorValuePercentage: valuePercentage
          },
          geometry: {
            type: 'Point',
            coordinates: [deviceWithValues.device.longitude, deviceWithValues.device.latitude]
          }
        });
      }

      if (this.map && this.mapLoaded) {
        (this.map.getSource('sensorData') as GeoJSONSource).setData(featureCollection);
      }
    }
  }

  updateDeviceMarkers(): void {
    if (!this.map || !this.sensorsContainer) {
      return;
    }

    this.sensorsContainer.clear();
    this.sensorRefs = [];
    this.devicesToMarkersMap.clear();
    if (this._devicesWithValues) {
      for (let deviceWithValues of this._devicesWithValues) {
        let componentRef = null;
        let anchor: Anchor = 'center';
        let offset: PointLike = [0, 0];

        if (deviceWithValues.device?.lastContact?.sensorType == 'PLANT_SENSOR') {
          componentRef = this.sensorsContainer.createComponent(PlantSensorComponent);
          anchor = 'bottom';
          offset = [0, 12.11];
        } else if (deviceWithValues.device?.lastContact?.sensorType == 'CLIMATE_SENSOR') {
          componentRef = this.sensorsContainer.createComponent(ClimateSensorComponent);
        } else {
          componentRef = this.sensorsContainer.createComponent(UnknownSensorComponent);
        }
        componentRef.instance.deviceWithValues = deviceWithValues;
        componentRef.instance.selectable = this.allowSensorSelection;

        const marker = new mapboxgl.Marker(componentRef.location.nativeElement, {
          anchor,
          offset,
          draggable: false
        })
          .setLngLat([deviceWithValues.device.longitude, deviceWithValues.device.latitude])
          .addTo(this.map);
        this.devicesToMarkersMap.set(deviceWithValues.device, marker);

        this.sensorRefs.push(componentRef);
      }
    }
  }
}
