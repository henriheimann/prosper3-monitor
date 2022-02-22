package de.p3monitor.weather;

import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.weather.requests.AveragedWeatherRequest;
import de.p3monitor.weather.requests.WeatherRequest;
import de.p3monitor.weather.responses.AveragedWeatherResponse;
import de.p3monitor.weather.responses.WeatherValuesResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class WeatherService
{
	private final InfluxDbService influxDbService;

	public Mono<WeatherValuesResponse> getWeather(WeatherRequest weatherRequest)
	{
		return influxDbService.getLatestCityWeather(weatherRequest.getCity())
				.map(influxWeatherValues -> new WeatherValuesResponse(
						influxWeatherValues.getCloudiness(),
						influxWeatherValues.getHumidity(),
						influxWeatherValues.getTemperature(),
						influxWeatherValues.getRain()
				));
	}

	public Mono<AveragedWeatherResponse> getAveragedWeather(AveragedWeatherRequest averagedWeatherRequest)
	{
		return influxDbService.getAveragedCityWeather(averagedWeatherRequest.getCity(),
						averagedWeatherRequest.getStart(), averagedWeatherRequest.getStop())
				.map(influxWeatherValues -> new AveragedWeatherResponse(
						averagedWeatherRequest.getStart(),
						averagedWeatherRequest.getStop(),
						new WeatherValuesResponse(
								influxWeatherValues.getCloudiness(),
								influxWeatherValues.getHumidity(),
								influxWeatherValues.getTemperature(),
								influxWeatherValues.getRain()
						)
				));
	}
}
