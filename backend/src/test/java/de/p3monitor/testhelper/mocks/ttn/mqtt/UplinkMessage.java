package de.p3monitor.testhelper.mocks.ttn.mqtt;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UplinkMessage
{
	@Builder.Default
	private String deviceId = "device-id";

	@Builder.Default
	private String applicationId = "ttn-test-app-id";

	@Builder.Default
	private String mqttUsername = "ttn-mqtt-username";

	@Builder.Default
	private int sensorType = 0;

	@Builder.Default
	private double temperature = 20.0;

	@Builder.Default
	private double humidity = 50.0;

	@Builder.Default
	private double irTemperature = 21.0;

	@Builder.Default
	private double brightnessCurrent = 100000;

	@Builder.Default
	private double batteryVoltage = 3.2;

	@Builder.Default
	private long moistureCounter = 5000;
}
