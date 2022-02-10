package de.p3monitor.influxdb.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WeatherValuesResponse
{
	private final LocalDateTime timestamp;
	private final String city;
	private final Long cloudiness;
	private final Long humidity;
	private final Double temperature;
	private final Double rain;
}
