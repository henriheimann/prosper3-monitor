package org.urbanenvironmentmonitor.ttn.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DeviceAddressResponse
{
	@JsonProperty(value = "dev_addr")
	private String deviceAddress;
}
