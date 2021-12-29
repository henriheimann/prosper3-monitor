package org.urbanenvironmentmonitor.testdata;

import lombok.experimental.UtilityClass;
import org.urbanenvironmentmonitor.ttn.api.dtos.EndDevice;

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
