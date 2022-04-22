package de.p3monitor.devices.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse
{
	private long id;
	private Long qrCodeId;
	private String name;
	private String ttnId;
	private String ttnDeviceAddress;
	private String ttnNetworkSessionKey;
	private String ttnApplicationSessionKey;
	private double latitude;
	private double longitude;
	private double brightnessMin;
	private double brightnessMax;
	private double moistureCounterMin;
	private double moistureCounterMax;
	private LastContactResponse lastContact;
}
