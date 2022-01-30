package de.p3monitor.mock.ttn.mqtt;

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
		byte[] payload = new byte[11];
		payload[0] = (byte)(((short)(uplinkMessage.getTemperature() * 100.0) & 0x00ff));
		payload[1] = (byte)(((short)(uplinkMessage.getTemperature() * 100.0) & 0xff00) >> 8);
		payload[2] = (byte)(((short)(uplinkMessage.getHumidity() * 100.0) & 0x00ff));
		payload[3] = (byte)(((short)(uplinkMessage.getHumidity() * 100.0) & 0xff00) >> 8);
		payload[4] = (byte)(((short)(uplinkMessage.getIrTemperature() * 100.0) & 0x00ff));
		payload[5] = (byte)(((short)(uplinkMessage.getIrTemperature() * 100.0) & 0xff00) >> 8);
		payload[6] = (byte)(((int)uplinkMessage.getBrightnessCurrent() & 0x000000ff));
		payload[7] = (byte)(((int)uplinkMessage.getBrightnessCurrent() & 0x0000ff00) >> 8);
		payload[8] = (byte)(((int)uplinkMessage.getBrightnessCurrent() & 0x00ff0000) >> 16);
		payload[9] = (byte)(((int)uplinkMessage.getBrightnessCurrent() & 0xff000000) >> 24);
		payload[10] = (byte)(uplinkMessage.getBatteryVoltage() * 10.0);

		String uplinkMessageTemplate = FileCopyUtils.copyToString(this.uplinkMessageTemplate.getInputStream(),
				Charset.defaultCharset());

		String uplinkJson = uplinkMessageTemplate
				.replace("{{device_id}}", uplinkMessage.getDeviceId())
				.replace("{{application_id}}", uplinkMessage.getApplicationId())
				.replace("{{payload_base64}}", Base64.getEncoder().encodeToString(payload));

		MqttMessage message = new MqttMessage(uplinkJson.getBytes());
		mockTtnMqttClient.publish("v3/" + uplinkMessage.getApplicationId() + "/devices/" +
				uplinkMessage.getDeviceId() + "/up", message);
	}
}
