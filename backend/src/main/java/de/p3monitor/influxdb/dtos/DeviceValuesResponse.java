package de.p3monitor.influxdb.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceValuesResponse
{
	private final LocalDateTime timestamp;
	private final DeviceSensorType sensorType;
	private final Double batteryVoltage;
	private final Double moistureCounter;
	private final Double temperature;
	private final Double humidity;
	private final Double irTemperature;
	private final Double brightnessCurrent;
}
