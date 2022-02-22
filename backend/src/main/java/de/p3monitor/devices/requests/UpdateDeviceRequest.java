package de.p3monitor.devices.requests;

import lombok.Data;

@Data
public class UpdateDeviceRequest
{
	private String name;
	private Double latitude;
	private Double longitude;
}
