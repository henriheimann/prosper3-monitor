import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVisualisations from './visualisation.reducer';
import { VisualisationState } from './visualisation.reducer';
import { MeasurementTypeModel } from '../models/measurement-type.model';

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

export const selectAllMeasurementsAtSelectedIndexForSelectedDevice = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => {
    if (!state.measurements || !state.selectedDevice) {
      return null;
    }

    let timestamp = state.measurements[state.selectedMeasurementIndex].timestamp;
    let sensorValues = state.measurements[state.selectedMeasurementIndex].sensorValues[state.selectedDevice.id];

    return {
      timestamp,
      sensorValues
    };
  }
);

export interface SelectedMeasurementsAtSelectedIndex {
  timestamp: string;
  type: MeasurementTypeModel;
  sensorValues: { [device_id: number]: number };
}

export const selectSelectedMeasurementsAtSelectedIndex = createSelector(
  selectVisualisationState,
  (state: VisualisationState) => {
    if (!state.measurements) {
      return null;
    }

    let timestamp = state.measurements[state.selectedMeasurementIndex].timestamp;
    let sensorValues = state.measurements[state.selectedMeasurementIndex].sensorValues;

    let sensorValuesModified: { [device_id: number]: number } = {};

    Object.entries(sensorValues).forEach(([deviceId, deviceSensorValues]) => {
      let parsedDevice = parseInt(deviceId);
      if (parsedDevice) {
        switch (state.selectedMeasurementType) {
          default:
          case MeasurementTypeModel.TEMPERATURE:
            if (deviceSensorValues.tmp) sensorValuesModified[parsedDevice] = deviceSensorValues.tmp;
            break;
          case MeasurementTypeModel.BRIGHTNESS_CURRENT:
            if (deviceSensorValues.bgh) sensorValuesModified[parsedDevice] = deviceSensorValues.bgh;
            break;
          case MeasurementTypeModel.HUMIDITY:
            if (deviceSensorValues.hum) sensorValuesModified[parsedDevice] = deviceSensorValues.hum;
            break;
        }
      }
    });

    return {
      timestamp,
      type: state.selectedMeasurementType,
      sensorValues: sensorValuesModified
    };
  }
);
