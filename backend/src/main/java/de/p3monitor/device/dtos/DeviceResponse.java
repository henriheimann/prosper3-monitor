package de.p3monitor.device.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.p3monitor.device.DeviceEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse
{
	private final long id;
	private final String name;
	private final String ttnId;
	private final String ttnDeviceAddress;
	private final String ttnNetworkSessionKey;
	private final String ttnApplicationSessionKey;
	private final LocalDateTime lastContact;

	public DeviceResponse(DeviceEntity deviceEntity, LocalDateTime lastContact)
	{
		this.id = deviceEntity.getId();
		this.name = deviceEntity.getName();
		this.ttnId = deviceEntity.getTtnId();
		this.ttnDeviceAddress = deviceEntity.getTtnDeviceAddress();
		this.ttnNetworkSessionKey = deviceEntity.getTtnNetworkSessionKey();
		this.ttnApplicationSessionKey = deviceEntity.getTtnApplicationSessionKey();
		this.lastContact = lastContact;
	}
}
