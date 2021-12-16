package org.urbanenvironmentmonitor.device.controllers.dtos;

import lombok.Getter;
import org.urbanenvironmentmonitor.device.entities.Device;

@Getter
public class DeviceResponse
{
	private final long id;
	private final String name;

	public DeviceResponse(Device deviceEntity)
	{
		this.id = deviceEntity.getId();
		this.name = deviceEntity.getName();
	}
}
