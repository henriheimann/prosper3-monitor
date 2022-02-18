import { createReducer, on } from '@ngrx/store';
import { DeviceModel } from '../../shared/models/device.model';
import * as VisualisationsActions from './visualisation.actions';
import { MeasurementTimespanModel } from '../models/measurement-timespan.model';
import { MeasurementTypeModel } from '../models/measurement-type.model';
import { MeasurementsModel } from '../models/measurements.model';

export const visualisationFeatureKey = 'visualisations';

export interface VisualisationState {
  selectedDevice: DeviceModel | null;
  inDraggingMode: boolean;

  measurements: MeasurementsModel[] | null;
  measurementsLoading: boolean;
  selectedTimespan: MeasurementTimespanModel;
  selectedMeasurementType: MeasurementTypeModel;
  selectedMeasurementIndex: number;
}

export const initialState: VisualisationState = {
  selectedDevice: null,
  inDraggingMode: false,

  measurements: null,
  measurementsLoading: false,
  selectedTimespan: MeasurementTimespanModel.LAST_DAY,
  selectedMeasurementType: MeasurementTypeModel.TEMPERATURE,
  selectedMeasurementIndex: 0
};

export const reducer = createReducer(
  initialState,

  on(VisualisationsActions.selectDevice, (state, action) => {
    return {
      ...state,
      selectedDevice: action.device
    };
  }),

  on(VisualisationsActions.selectMeasurements, (state, action) => {
    return {
      ...state,
      measurementsLoading: true
    };
  }),

  on(VisualisationsActions.selectMeasurementsSuccess, (state, action) => {
    return {
      ...state,
      measurements: action.measurements,
      measurementsLoading: false,
      selectedTimespan: action.measurementTimespan,
      selectedMeasurementType: action.measurementType
    };
  }),

  on(VisualisationsActions.selectMeasurementsFailure, (state, action) => {
    return {
      ...state,
      measurements: null,
      measurementsLoading: false,
      selectedTimespan: MeasurementTimespanModel.LAST_DAY,
      selectedMeasurementType: MeasurementTypeModel.TEMPERATURE
    };
  }),

  on(VisualisationsActions.selectMeasurementsIndex, (state, action) => {
    return {
      ...state,
      selectedMeasurementIndex: action.measurementsIndex
    };
  }),

  on(VisualisationsActions.startDraggingSelectedDevice, (state) => {
    return {
      ...state,
      inDraggingMode: true
    };
  }),

  on(VisualisationsActions.stopDraggingSelectedDevice, (state) => {
    return {
      ...state,
      inDraggingMode: false
    };
  })
);
