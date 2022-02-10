import { createAction, props } from '@ngrx/store';
import { DeviceModel } from '../../shared/models/device.model';

export const selectDevice = createAction('[Visualisations] Select Device', props<{ device: DeviceModel | null }>());

export const startDraggingSelectedDevice = createAction('[Visualisations] Start Dragging Selected Device');

export const stopDraggingSelectedDevice = createAction('[Visualisations] Stop Dragging Selected Device');
