package de.p3monitor.device.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.p3monitor.influxdb.dtos.DeviceValuesResponse;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse
{
	private long id;
	private String name;
	private String ttnId;
	private String ttnDeviceAddress;
	private String ttnNetworkSessionKey;
	private String ttnApplicationSessionKey;
	private double latitude;
	private double longitude;
	private DeviceValuesResponse lastContact;
}
