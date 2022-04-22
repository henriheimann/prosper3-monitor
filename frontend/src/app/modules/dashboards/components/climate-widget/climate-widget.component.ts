import { Component } from '@angular/core';
import { MeasurementTimespanModel } from '../../../sensor-maps/models/measurement-timespan.model';

@Component({
  selector: 'p3m-climate-widget',
  templateUrl: './climate-widget.component.html',
  styleUrls: ['./climate-widget.component.sass']
})
export class ClimateWidgetComponent {
  measurementTimespanModels = [MeasurementTimespanModel.LAST_DAY, MeasurementTimespanModel.LAST_WEEK, MeasurementTimespanModel.LAST_MONTH, MeasurementTimespanModel.LAST_YEAR];
  selectedMeasurementTimespan = MeasurementTimespanModel.LAST_DAY;

  onTimespanSelected(value: MeasurementTimespanModel) {
    this.selectedMeasurementTimespan = value;
  }
}
