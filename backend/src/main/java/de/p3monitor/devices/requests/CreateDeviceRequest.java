package de.p3monitor.devices.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDeviceRequest
{
	@NotBlank
	private String name;

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;

	private Long qrCodeId;

	private Double brightnessMin;

	private Double brightnessMax;

	private Double moistureCounterMin;

	private Double moistureCounterMax;
}
