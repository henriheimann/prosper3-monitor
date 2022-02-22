package de.p3monitor.devices.responses;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
public class MeasurementsResponse
{
	private ZonedDateTime timestamp;
	private Map<Long, DeviceValuesResponse> deviceValues;
}
