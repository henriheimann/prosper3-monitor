package org.urbanenvironmentmonitor.ttn.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.urbanenvironmentmonitor.ttn.mqtt.config.TtnMqttClientProperties;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.client.ReactiveHttpRequestInterceptors;


@Configuration
@EnableConfigurationProperties({TtnApiClientProperties.class})
public class TtnApiClientConfig
{
	@Bean
	public ReactiveHttpRequestInterceptor bearerTokenAuthRequestInterceptor(TtnApiClientProperties properties)
	{
		return ReactiveHttpRequestInterceptors.addHeader("Authentication", "Bearer " + properties.getToken());
	}

	@Bean
	public ObjectMapper objectMapper()
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
		return objectMapper;
	}
}
