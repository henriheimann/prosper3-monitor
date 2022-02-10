package de.p3monitor.device.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceRequest
{
	@NotBlank
	private String name;

	@NotBlank
	private Double latitude;

	@NotBlank
	private Double longitude;
}
