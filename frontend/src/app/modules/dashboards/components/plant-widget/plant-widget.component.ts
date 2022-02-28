import { Component } from '@angular/core';
import { MeasurementTimespanModel } from '../../../sensor-maps/models/measurement-timespan.model';

@Component({
  selector: 'p3m-plant-widget',
  templateUrl: './plant-widget.component.html',
  styleUrls: ['./plant-widget.component.sass']
})
export class PlantWidgetComponent {
  measurementTimespanModel = MeasurementTimespanModel;
  selectedMeasurementTimespan = MeasurementTimespanModel.LAST_DAY;

  onTimespanSelected(value: MeasurementTimespanModel) {
    this.selectedMeasurementTimespan = value;
  }
}
