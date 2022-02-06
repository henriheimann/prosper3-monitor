export interface DeviceModel {
  id: number;
  name: string;
  ttnId?: string;
  ttnDeviceAddress?: string;
  ttnNetworkSessionKey?: string;
  ttnApplicationSessionKey?: string;
  lastContact?: string;
}
