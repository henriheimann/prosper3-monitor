package de.p3monitor.testdata;

import de.p3monitor.ttn.api.dtos.EndDevice;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TestEndDevices
{
	public static EndDevice create(String id, String eui, LocalDateTime created, LocalDateTime updated)
	{
		EndDevice endDevice = new EndDevice();
		endDevice.setIds(new EndDevice.Identifiers(id, eui));
		endDevice.setCreatedAt(created);
		endDevice.setUpdatedAt(updated);
		return endDevice;
	}
}
