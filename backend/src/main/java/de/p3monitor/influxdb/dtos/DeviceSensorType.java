package de.p3monitor.influxdb.dtos;

public enum DeviceSensorType
{
	CLIMATE_SENSOR,
	PLANT_SENSOR;

	public static DeviceSensorType fromId(long id)
	{
		if (id == 0) {
			return CLIMATE_SENSOR;
		} else {
			return PLANT_SENSOR;
		}
	}
}
