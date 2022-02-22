package de.p3monitor.influxdb.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class InfluxDeviceValues
{
	private final ZonedDateTime timestamp;
	private final String ttnDeviceId;
	private final DeviceSensorType sensorType;
	private final Double batteryVoltage;
	private final Double moistureCounter;
	private final Double temperature;
	private final Double humidity;
	private final Double irTemperature;
	private final Double brightnessCurrent;
}
