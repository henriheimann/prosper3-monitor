import { Component, OnInit } from '@angular/core';
import { Options } from '@angular-slider/ngx-slider';
import { MeasurementTypeModel } from '../../models/measurement-type.model';
import { Store } from '@ngrx/store';
import {
  selectAreMeasurementsLoading,
  selectSelectedMeasurements,
  selectSelectedMeasurementType,
  selectSelectedTimespan
} from '../../store/visualisation.selectors';
import { MeasurementTimespanModel } from '../../models/measurement-timespan.model';
import { MeasurementsModel } from '../../models/measurements.model';
import { Observable } from 'rxjs';
import { selectMeasurements, selectMeasurementsIndex } from '../../store/visualisation.actions';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'p3m-measurements-selector',
  templateUrl: './measurements-selector.component.html',
  styleUrls: ['./measurements-selector.component.sass']
})
export class MeasurementsSelectorComponent implements OnInit {
  measurementType = MeasurementTypeModel;
  measurementTimespan = MeasurementTimespanModel;

  selectedTimespan$: Observable<MeasurementTimespanModel> = this.store.select(selectSelectedTimespan);
  selectedTimespan: MeasurementTimespanModel | null = null;
  selectedType$: Observable<MeasurementTypeModel> = this.store.select(selectSelectedMeasurementType);
  selectedType: MeasurementTypeModel | null = null;

  measurements$: Observable<MeasurementsModel[] | null> = this.store.select(selectSelectedMeasurements);
  measurements: MeasurementsModel[] | null = null;

  areMeasurementsLoading$ = this.store.select(selectAreMeasurementsLoading);

  sliderSelectedIndex = 0;
  sliderOptions: Options = {
    stepsArray: [
      {
        value: 0
      }
    ],
    translate: (value: number): string => {
      if (this.measurements && this.measurements[value].timestamp) {
        return this.datePipe.transform(new Date(this.measurements[value].timestamp), 'short') || '';
      } else {
        return '';
      }
    }
  };

  constructor(private store: Store, private datePipe: DatePipe) {}

  onSliderChange(): void {
    this.store.dispatch(
      selectMeasurementsIndex({
        measurementsIndex: this.sliderSelectedIndex
      })
    );
  }

  ngOnInit(): void {
    this.measurements$.subscribe((measurements) => {
      if (measurements) {
        const newOptions: Options = Object.assign({}, this.sliderOptions);
        newOptions.stepsArray = measurements.map((_, index) => {
          return {
            value: index
          };
        });
        this.sliderOptions = newOptions;
      }
      this.measurements = measurements;
    });
    this.selectedTimespan$.subscribe((selectedTimespan) => (this.selectedTimespan = selectedTimespan));
    this.selectedType$.subscribe((selectedType) => (this.selectedType = selectedType));

    this.store.dispatch(
      selectMeasurements({
        measurementTimespan: MeasurementTimespanModel.LAST_DAY,
        measurementType: MeasurementTypeModel.TEMPERATURE
      })
    );
  }

  onTimespanClicked(newTimespan: MeasurementTimespanModel): void {
    this.store.dispatch(
      selectMeasurements({
        measurementTimespan: newTimespan,
        measurementType: this.selectedType || MeasurementTypeModel.TEMPERATURE
      })
    );
  }

  onTypeClicked(newType: MeasurementTypeModel): void {
    this.store.dispatch(
      selectMeasurements({
        measurementTimespan: this.selectedTimespan || MeasurementTimespanModel.LAST_DAY,
        measurementType: newType
      })
    );
  }
}
