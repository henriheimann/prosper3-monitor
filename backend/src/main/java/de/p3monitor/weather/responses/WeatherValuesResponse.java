package de.p3monitor.weather.responses;

import lombok.Data;

@Data
public class WeatherValuesResponse
{
	private final Double cld;
	private final Double hum;
	private final Double tmp;
	private final Double rai;
}
