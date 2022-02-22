import { createAction, props } from '@ngrx/store';
import { DeviceModel } from '../../shared/models/device.model';
import { MeasurementTimespanModel } from '../models/measurement-timespan.model';
import { MeasurementTypeModel } from '../models/measurement-type.model';
import { HttpErrorResponse } from '@angular/common/http';
import { MeasurementsModel } from '../models/measurements.model';

export const selectDevice = createAction('[Visualisations] Select Device', props<{ device: DeviceModel | null }>());

export const selectMeasurements = createAction(
  '[Visualisation] Select Measurements',
  props<{
    measurementTimespan: MeasurementTimespanModel;
    measurementType: MeasurementTypeModel;
  }>()
);

export const selectMeasurementsSuccess = createAction(
  '[Visualisations] Select Measurements Success',
  props<{
    measurementTimespan: MeasurementTimespanModel;
    measurementType: MeasurementTypeModel;
    measurements: MeasurementsModel[];
  }>()
);

export const selectMeasurementsFailure = createAction(
  '[Visualisations] Select Measurements Failure',
  props<{ error: HttpErrorResponse }>()
);

export const selectMeasurementsIndex = createAction(
  '[Visualisation] Select Measurements Index',
  props<{
    measurementsIndex: number;
  }>()
);

export const startDraggingSelectedDevice = createAction('[Visualisations] Start Dragging Selected Device');

export const stopDraggingSelectedDevice = createAction('[Visualisations] Stop Dragging Selected Device');

export const startIdentifyingDevice = createAction('[Visualisations] Start Identifying Device');

export const stopIdentifyingDevice = createAction('[Visualisations] Stop Identifying Device');

export const deviceIdentified = createAction(
  '[Visualisations] Device Identified',
  props<{ device: DeviceModel | null }>()
);
