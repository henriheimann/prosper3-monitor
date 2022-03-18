package de.p3monitor.testhelper.data;

import de.p3monitor.ttn.api.dtos.EndDevice;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TestEndDevices
{
	public static EndDevice create(String id, String eui, String deviceAddress, String appSessionKey,
	                               String networkSessionKey, LocalDateTime created, LocalDateTime updated)
	{
		EndDevice endDevice = new EndDevice();
		endDevice.setIds(new EndDevice.Identifiers(id, eui));
		endDevice.setCreatedAt(created);
		endDevice.setUpdatedAt(updated);
		endDevice.setSession(new EndDevice.Session(new EndDevice.Session.Keys(
			new EndDevice.Session.Keys.Key(appSessionKey),
			new EndDevice.Session.Keys.Key(networkSessionKey)
		), deviceAddress));
		return endDevice;
	}
}
