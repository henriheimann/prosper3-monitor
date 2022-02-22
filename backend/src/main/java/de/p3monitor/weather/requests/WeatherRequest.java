package de.p3monitor.weather.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WeatherRequest
{
	@NotNull
	private String city;
}
