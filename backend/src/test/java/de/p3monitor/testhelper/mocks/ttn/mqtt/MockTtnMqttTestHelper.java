package de.p3monitor.testhelper.mocks.ttn.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.flywaydb.core.internal.util.FileCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Base64;

@Component
public class MockTtnMqttTestHelper
{
	@Autowired
	@Qualifier("mockTtnMqttClient")
	private MqttClient mockTtnMqttClient;

	@Value("classpath:ttn/mqtt/uplink_message_template.json")
	Resource uplinkMessageTemplate;

	public void publishUplinkMessage(UplinkMessage uplinkMessage) throws Exception
	{
		byte[] payload = new byte[12];

		String uplinkMessageTemplate = FileCopyUtils.copyToString(this.uplinkMessageTemplate.getInputStream(),
				Charset.defaultCharset());

		String uplinkJson = uplinkMessageTemplate
				.replace("{{device_id}}", uplinkMessage.getDeviceId())
				.replace("{{application_id}}", uplinkMessage.getApplicationId())
				.replace("{{payload_base64}}", Base64.getEncoder().encodeToString(payload))
				.replace("{{sensor_type}}", String.valueOf(uplinkMessage.getSensorType()))
				.replace("{{battery_voltage}}", String.valueOf(uplinkMessage.getBatteryVoltage()))
				.replace("{{moisture_counter}}", String.valueOf(uplinkMessage.getMoistureCounter()))
				.replace("{{temperature}}", String.valueOf(uplinkMessage.getTemperature()))
				.replace("{{humidity}}", String.valueOf(uplinkMessage.getHumidity()))
				.replace("{{ir_temperature}}", String.valueOf(uplinkMessage.getIrTemperature()))
				.replace("{{brightness_current}}", String.valueOf(uplinkMessage.getBrightnessCurrent()));

		MqttMessage message = new MqttMessage(uplinkJson.getBytes());
		mockTtnMqttClient.publish("v3/" + uplinkMessage.getMqttUsername() + "/devices/" +
				uplinkMessage.getDeviceId() + "/up", message);
	}
}
