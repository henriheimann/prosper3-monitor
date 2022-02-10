package de.p3monitor.weather;

import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.influxdb.dtos.WeatherValuesResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class WeatherService
{
	private final InfluxDbService influxDbService;

	public Mono<WeatherValuesResponse> getWeather(String city)
	{
		return influxDbService.getLatestCityWeather(city);
	}
}
