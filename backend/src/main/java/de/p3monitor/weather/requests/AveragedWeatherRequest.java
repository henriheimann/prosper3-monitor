package de.p3monitor.weather.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
public class AveragedWeatherRequest
{
	@NotNull
	private String city;

	@NotNull
	private ZonedDateTime start;

	@NotNull
	private ZonedDateTime stop;
}
