package de.p3monitor.weather;

import de.p3monitor.influxdb.dtos.WeatherValuesResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weather")
@AllArgsConstructor
public class WeatherController
{
	private final WeatherService weatherService;

	@PostMapping("/{city}")
	public Mono<WeatherValuesResponse> createDevice(@PathVariable String city)
	{
		return weatherService.getWeather(city);
	}
}
