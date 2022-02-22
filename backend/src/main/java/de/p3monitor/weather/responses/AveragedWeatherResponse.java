package de.p3monitor.weather.responses;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AveragedWeatherResponse
{
	private final ZonedDateTime start;
	private final ZonedDateTime stop;
	private final WeatherValuesResponse weatherValues;
}
