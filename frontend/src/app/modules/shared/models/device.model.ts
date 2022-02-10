export interface DeviceModel {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  ttnId?: string;
  ttnDeviceAddress?: string;
  ttnNetworkSessionKey?: string;
  ttnApplicationSessionKey?: string;
  lastContact?: DeviceValues;
}

export interface DeviceValues {
  timestamp: string;
  sensorType: string;
  batteryVoltage?: number;
  moistureCounter?: number;
  temperature?: number;
  humidity?: number;
  irTemperature?: number;
  brightnessCurrent?: number;
}
