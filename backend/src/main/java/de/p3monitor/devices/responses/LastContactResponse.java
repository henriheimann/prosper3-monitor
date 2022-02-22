package de.p3monitor.devices.responses;

import de.p3monitor.influxdb.dtos.DeviceSensorType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class LastContactResponse
{
	private ZonedDateTime timestamp;
	private DeviceSensorType sensorType;
	private DeviceValuesResponse deviceValues;
}
