package de.p3monitor.weather;

import de.p3monitor.influxdb.dtos.InfluxWeatherValues;
import de.p3monitor.weather.requests.AveragedWeatherRequest;
import de.p3monitor.weather.requests.WeatherRequest;
import de.p3monitor.weather.responses.AveragedWeatherResponse;
import de.p3monitor.weather.responses.WeatherValuesResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/weather")
@AllArgsConstructor
public class WeatherController
{
	private final WeatherService weatherService;

	@PostMapping("")
	public Mono<WeatherValuesResponse> getWeather(@RequestBody @Valid WeatherRequest weatherRequest)
	{
		return weatherService.getWeather(weatherRequest);
	}

	@PostMapping("/averaged")
	public Mono<AveragedWeatherResponse> getWeather(@RequestBody @Valid AveragedWeatherRequest averagedWeatherRequest)
	{
		return weatherService.getAveragedWeather(averagedWeatherRequest);
	}
}
