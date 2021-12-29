package org.urbanenvironmentmonitor.mock.ttn.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MockTtnMqttClientProperties.class})
public class MockTtnMqttClientConfig
{
	@Bean(initMethod = "connect", destroyMethod = "disconnect")
	@Qualifier("mockTtnMqttClient")
	public MqttClient mockTtnMqttClient(MockTtnMqttClientProperties properties) throws MqttException
	{
		return new MqttClient(properties.getUrl(), properties.getClientId());
	}
}
