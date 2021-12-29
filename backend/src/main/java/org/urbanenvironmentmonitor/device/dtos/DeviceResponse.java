package org.urbanenvironmentmonitor.device.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse
{
	private final long id;
	private final String name;

	private final TtnSyncResponse ttnSync;

	public DeviceResponse(long id, String name)
	{
		this.id = id;
		this.name = name;
		this.ttnSync = null;
	}

	public DeviceResponse(long id, String name, TtnSyncResponse ttnSync)
	{
		this.id = id;
		this.name = name;
		this.ttnSync = ttnSync;
	}
}
