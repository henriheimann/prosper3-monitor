package de.p3monitor.device.dtos;

import de.p3monitor.influxdb.dtos.DeviceSensorType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class LastContactResponse
{
	private ZonedDateTime timestamp;
	private DeviceSensorType sensorType;
	private SensorValuesResponse sensorValues;
}
