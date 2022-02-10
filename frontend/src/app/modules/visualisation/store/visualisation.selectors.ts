import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVisualisations from './visualisation.reducer';
import { VisualisationState } from './visualisation.reducer';

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
