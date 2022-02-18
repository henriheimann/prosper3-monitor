import { LastContactModel } from './last-contact.model';

export interface DeviceModel {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  ttnId?: string;
  ttnDeviceAddress?: string;
  ttnNetworkSessionKey?: string;
  ttnApplicationSessionKey?: string;
  lastContact?: LastContactModel;
}
