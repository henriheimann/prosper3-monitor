import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVisualisations from './visualisation.reducer';
import { VisualisationState } from './visualisation.reducer';
import { MeasurementTypeModel } from '../models/measurement-type.model';
import { DeviceWithValuesModel } from '../models/device-with-values.model';

export const selectVisualisationState = createFeatureSelector<fromVisualisations.VisualisationState>(
  fromVisualisations.visualisationFeatureKey
);

export const selectSelectedDevice = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => state.selectedDevice
);

export const selectDraggingDevice = createSelector(selectVisualisationState, (state: VisualisationState) =>
  state.inDraggingMode ? state.selectedDevice : null
);

export const selectSelectedMeasurements = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => state.measurements
);

export const selectSelectedTimespan = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => state.selectedTimespan
);

export const selectSelectedMeasurementType = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => state.selectedMeasurementType
);

export const selectSelectedDeviceValuesForIndex = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => {
    if (!state.measurements || !state.selectedDevice) {
      return null;
    }

    let timestamp = state.measurements[state.selectedMeasurementIndex].timestamp;
    let deviceValues = state.measurements[state.selectedMeasurementIndex].deviceValues[state.selectedDevice.id];

    return {
      timestamp,
      deviceValues
    };
  }
);

export const selectAllDeviceValuesForIndex = createSelector(selectVisualisationState, (state: VisualisationState) => {
  if (!state.measurements) {
    return null;
  }

  let timestamp = state.measurements[state.selectedMeasurementIndex].timestamp;
  let deviceValues = state.measurements[state.selectedMeasurementIndex].deviceValues;

  return {
    timestamp,
    deviceValues
  };
});

export const selectIsIdentifyingDevice = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => state.inIdentifyingMode
);

export const selectAreMeasurementsLoading = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => state.measurementsLoading
);
