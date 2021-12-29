#pragma once

#include <stdint.h>

typedef enum
{
	CLIMATE_SENSOR,
	IRRIGATION_SENSOR
} sensor_type_t;

typedef struct
{
	uint8_t type;
	uint8_t battery_voltage;
	int16_t temperature;
	union
	{
		struct
		{
			uint16_t humidity;
			int16_t ir_temperature;
			uint32_t brightness_current;
		};
		struct
		{
			uint32_t moisture_counter;
		};
	};
} __packed data_packet_t;
