package de.p3monitor.testdata;

import lombok.experimental.UtilityClass;
import de.p3monitor.device.DeviceEntity;

@UtilityClass
public class TestDeviceEntities
{
	public static DeviceEntity create(long id, String name, String ttnId)
	{
		DeviceEntity deviceEntity = new DeviceEntity();
		deviceEntity.setId(1L);
		deviceEntity.setName("name");
		deviceEntity.setTtnId(ttnId);
		return deviceEntity;
	}
}
