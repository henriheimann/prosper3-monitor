package de.p3monitor.devices.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
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
	private LastContactResponse lastContact;
}
