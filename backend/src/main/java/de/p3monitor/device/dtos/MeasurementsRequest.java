package de.p3monitor.device.dtos;

import de.p3monitor.influxdb.dtos.DeviceSensorType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
public class MeasurementsRequest
{
	private DeviceSensorType deviceSensorType;

	@NotNull
	private ZonedDateTime start;

	@NotNull
	private ZonedDateTime stop;

	@NotNull
	private long aggregateWindowSeconds;
}
