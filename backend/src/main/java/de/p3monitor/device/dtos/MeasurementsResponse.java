package de.p3monitor.device.dtos;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
public class MeasurementsResponse
{
	private ZonedDateTime timestamp;
	private Map<Long, SensorValuesResponse> sensorValues;
}
