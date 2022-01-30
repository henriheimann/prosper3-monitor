package de.p3monitor.mock.ttn.mqtt;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UplinkMessage
{
	@Builder.Default
	private String deviceId = "device-id";              // eui-70b3d57ed0049fa1

	@Builder.Default
	private String applicationId = "application-id";    // urban-environment-monitor

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
}
