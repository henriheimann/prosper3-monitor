import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectSelectedMeasurementType } from '../../store/visualisation.selectors';
import {
  getMaximumValueForMeasurementType,
  getMinimumValueForMeasurementType,
  getUnitForMeasurementType
} from '../../models/measurement-type.model';

@Component({
  selector: 'p3m-map-color-legend',
  templateUrl: './map-color-legend.component.html',
  styleUrls: ['./map-color-legend.component.sass']
})
export class MapColorLegendComponent implements OnInit {
  measurementType$ = this.store.select(selectSelectedMeasurementType);

  unit: string = '';
  minimumValue: number = 0;
  maximumValue: number = 100;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.measurementType$.subscribe((measurementType) => {
      this.unit = getUnitForMeasurementType(measurementType);
      this.maximumValue = getMaximumValueForMeasurementType(measurementType);
      this.minimumValue = getMinimumValueForMeasurementType(measurementType);
      if (this.unit === '%') {
        this.maximumValue *= 100;
      }
    });
  }
}
