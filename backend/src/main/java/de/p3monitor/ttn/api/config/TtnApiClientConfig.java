package de.p3monitor.ttn.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import de.p3monitor.ttn.api.TtnApiApplicationClient;
import de.p3monitor.ttn.api.TtnApiEndDeviceClient;
import de.p3monitor.ttn.api.TtnApiNetworkServerClient;
import reactivefeign.client.ReactiveHttpRequestInterceptors;
import reactivefeign.webclient.WebReactiveFeign;

@Configuration
@EnableConfigurationProperties({TtnApiClientProperties.class})
public class TtnApiClientConfig
{
	private <T> T createClient(Class<T> clazz, TtnApiClientProperties properties)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());

		return WebReactiveFeign.<T>builder()
				.addRequestInterceptor(ReactiveHttpRequestInterceptors
						.addHeader("Authorization", "Bearer " + properties.getToken()))
				.addRequestInterceptor(ReactiveHttpRequestInterceptors
						.addHeader("User-Agent", "p3m-backed/0.0.1"))
				.objectMapper(objectMapper)
				.target(clazz, properties.getUrl());
	}

	@Bean
	public TtnApiApplicationClient ttnApiApplicationClient(TtnApiClientProperties properties)
	{
		return createClient(TtnApiApplicationClient.class, properties);
	}

	@Bean
	public TtnApiEndDeviceClient ttnApiEndDeviceClient(TtnApiClientProperties properties)
	{
		return createClient(TtnApiEndDeviceClient.class, properties);
	}

	@Bean
	public TtnApiNetworkServerClient ttnApiNetworkServerClient(TtnApiClientProperties properties)
	{
		return createClient(TtnApiNetworkServerClient.class, properties);
	}
}
