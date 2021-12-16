package org.urbanenvironmentmonitor.ttn.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TtnMqttClientProperties.class})
public class TtnMqttClientConfig
{
	@Bean(initMethod = "connect", destroyMethod = "disconnect")
	public MqttClient ttnMqttClient(TtnMqttClientProperties properties) throws MqttException
	{
		return new MqttClient(properties.getUrl(), properties.getClientId());
	}
}
