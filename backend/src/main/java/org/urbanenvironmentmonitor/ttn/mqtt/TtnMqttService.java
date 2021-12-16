package org.urbanenvironmentmonitor.ttn.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.urbanenvironmentmonitor.live.LiveMeasurementService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
public class TtnMqttService implements IMqttMessageListener
{
	private final MqttClient ttnMqttClient;
	private final LiveMeasurementService measurementService;

	public TtnMqttService(MqttClient ttnMqttClient, LiveMeasurementService measurementService)
	{
		this.ttnMqttClient = ttnMqttClient;
		this.measurementService = measurementService;
	}

	@PostConstruct
	public void postConstruct() throws MqttException
	{
		ttnMqttClient.subscribe("v3/+/devices/+/up", this);
	}

	@PreDestroy
	public void preDestroy() throws MqttException
	{
		ttnMqttClient.unsubscribe("v3/+/devices/+/up");
	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage)
	{
		measurementService.publishMeasurement(s);
	}
}
