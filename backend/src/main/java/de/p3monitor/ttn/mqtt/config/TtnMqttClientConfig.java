package de.p3monitor.ttn.mqtt.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties({TtnMqttClientProperties.class})
public class TtnMqttClientConfig
{
	@Bean(destroyMethod = "disconnect")
	public MqttClient ttnMqttClient(TtnMqttClientProperties properties) throws MqttException
	{
		MqttClient mqttClient = new MqttClient(properties.getUrl(), properties.getClientId());
		if (properties.getUsername() != null && properties.getPassword() != null) {
			MqttConnectOptions connectOptions = new MqttConnectOptions();
			connectOptions.setUserName(properties.getUsername());
			connectOptions.setPassword(properties.getPassword().toCharArray());
			mqttClient.connect(connectOptions);
		} else {
			mqttClient.connect();
		}
		return mqttClient;
	}
}
