import { Component } from '@angular/core';
import { MeasurementTimespanModel } from '../../../sensor-maps/models/measurement-timespan.model';

@Component({
  selector: 'p3m-climate-widget',
  templateUrl: './climate-widget.component.html',
  styleUrls: ['./climate-widget.component.sass']
})
export class ClimateWidgetComponent {
  measurementTimespanModel = MeasurementTimespanModel;

  selectedMeasurementTimespan = MeasurementTimespanModel.LAST_DAY;

  onTimespanSelected(value: MeasurementTimespanModel) {
    this.selectedMeasurementTimespan = value;
  }
}
