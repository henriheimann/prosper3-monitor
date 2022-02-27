package de.p3monitor.devices.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateDeviceRequest
{
	@NotBlank
	private String name;

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;
}
