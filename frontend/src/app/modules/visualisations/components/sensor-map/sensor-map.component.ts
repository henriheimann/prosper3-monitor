import { AfterViewInit, Component, ComponentRef, OnDestroy, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import * as mapboxgl from 'mapbox-gl';
import { PlantSensorComponent } from '../plant-sensor/plant-sensor.component';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'p3m-sensor-map',
  templateUrl: './sensor-map.component.html',
  styleUrls: ['./sensor-map.component.sass']
})
export class SensorMapComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sensorsContainer', { read: ViewContainerRef }) sensorsContainer: ViewContainerRef | undefined;

  sensorRefs: ComponentRef<PlantSensorComponent>[] = [];

  map: mapboxgl.Map | undefined;

  constructor() {}

  ngOnInit(): void {
    this.map = new mapboxgl.Map({
      container: 'map',
      zoom: 17,
      center: [6.932781, 51.532235],
      style: environment.tileserverStyleUrl
    });

    this?.map.addControl(new mapboxgl.NavigationControl());
  }

  ngAfterViewInit(): void {
    this.addSensor([6.933008, 51.532328]);
    this.addSensor([6.933112, 51.53232]);
  }

  addSensor(latLng: mapboxgl.LngLatLike): void {
    if (this.map && this.sensorsContainer) {
      const componentRef = this.sensorsContainer.createComponent(PlantSensorComponent);
      new mapboxgl.Marker(componentRef.location.nativeElement, {
        anchor: 'bottom',
        draggable: true
      })
        .setLngLat(latLng)
        .addTo(this.map);
      this.sensorRefs.push(componentRef);
    }
  }

  ngOnDestroy(): void {
    this.sensorRefs.forEach((sensorRef) => sensorRef.destroy());
  }
}
