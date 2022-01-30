package de.p3monitor.ttn.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EndDevicesResponse
{
	private List<EndDevice> endDevices;
}
