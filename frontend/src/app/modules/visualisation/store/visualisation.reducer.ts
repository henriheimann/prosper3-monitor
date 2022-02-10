import { createReducer, on } from '@ngrx/store';
import { DeviceModel } from '../../shared/models/device.model';
import * as VisualisationsActions from './visualisation.actions';

export const visualisationFeatureKey = 'visualisations';

export interface VisualisationState {
  selectedDevice: DeviceModel | null;
  inDraggingMode: boolean;
}

export const initialState: VisualisationState = {
  selectedDevice: null,
  inDraggingMode: false
};

export const reducer = createReducer(
  initialState,

  on(VisualisationsActions.selectDevice, (state, action) => {
    return {
      ...state,
      selectedDevice: action.device
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
