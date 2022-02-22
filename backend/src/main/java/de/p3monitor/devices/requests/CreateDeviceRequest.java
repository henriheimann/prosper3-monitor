package de.p3monitor.devices.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateDeviceRequest
{
	@NotBlank
	private String name;

	@NotBlank
	private Double latitude;

	@NotBlank
	private Double longitude;
}
