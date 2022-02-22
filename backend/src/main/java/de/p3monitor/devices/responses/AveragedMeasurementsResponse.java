package de.p3monitor.devices.responses;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AveragedMeasurementsResponse
{
	private ZonedDateTime start;
	private ZonedDateTime stop;
	private DeviceValuesResponse deviceValues;
}
