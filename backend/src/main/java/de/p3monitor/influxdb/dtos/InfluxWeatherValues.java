package de.p3monitor.influxdb.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class InfluxWeatherValues
{
	private final ZonedDateTime timestamp;
	private final String city;
	private final Double cloudiness;
	private final Double humidity;
	private final Double temperature;
	private final Double rain;
}
