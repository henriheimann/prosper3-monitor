package de.p3monitor.device.dtos;

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
