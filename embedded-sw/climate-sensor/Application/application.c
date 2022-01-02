#include "application.h"
#include "data_packet.h"

#include <stdio.h>
#include <i2c.h>
#include <spi.h>
#include <adc.h>
#include <math.h>

#ifndef TTN_KEYS_DEVICE_ADDRESS
#warning "TTN_KEYS_DEVICE_ADDRESS not defined, using default"
#define TTN_KEYS_DEVICE_ADDRESS {0x00, 0x00, 0x00, 0x00}
#endif

#ifndef TTN_KEYS_APPLICATION_SESSION_KEY
#warning "TTN_KEYS_APPLICATION_SESSION_KEY not defined, using default"
#define TTN_KEYS_APPLICATION_SESSION_KEY {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
#endif

#ifndef TTN_KEYS_NETWORK_SESSION_KEY
#warning "TTN_KEYS_NETWORK_SESSION_KEY not defined, using default"
#define TTN_KEYS_NETWORK_SESSION_KEY {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
#endif

static bool reload_frame_counter(uint16_t *tx_counter, uint16_t *rx_counter);
static void save_frame_counter(uint16_t tx_counter, uint16_t rx_counter);

sht3x_handle_t sht3x_handle = {
		.i2c_handle = &hi2c1,
		.device_address = SHT3X_I2C_DEVICE_ADDRESS_ADDR_PIN_LOW
};

rfm95_handle_t rfm95_handle = {
		.spi_handle = &hspi1,
		.nss_port = RFM95_NSS_GPIO_Port,
		.nss_pin = RFM95_NSS_Pin,
		.nrst_port = RFM95_NRST_GPIO_Port,
		.nrst_pin = RFM95_NRST_Pin,
		.irq_port = RFM95_DIO0_GPIO_Port,
		.irq_pin = RFM95_DIO0_Pin,
		.dio5_port = RFM95_DIO5_GPIO_Port,
		.dio5_pin = RFM95_DIO5_Pin,
		.device_address = TTN_KEYS_DEVICE_ADDRESS,
		.application_session_key = TTN_KEYS_APPLICATION_SESSION_KEY,
		.network_session_key = TTN_KEYS_NETWORK_SESSION_KEY,
		.reload_frame_counter = reload_frame_counter,
		.save_frame_counter = save_frame_counter
};

eeprom_handle_t eeprom_handle = {
		.i2c_handle = &hi2c1,
		.device_address = EEPROM_24LC32A_ADDRESS,
		.max_address = EEPROM_24LC32A_MAX_ADDRESS,
		.page_size = EEPROM_24LC32A_PAGE_SIZE
};

mlx90614_handle_t mlx90614_handle = {
		.i2c_handle = &hi2c1,
		.device_address = MLX90614_DEFAULT_ADDRESS
};

static bool reload_frame_counter(uint16_t *tx_counter, uint16_t *rx_counter)
{
	uint8_t buffer[6];

	if (!eeprom_read_bytes(&eeprom_handle, 0x20, buffer, sizeof(buffer))) {
		return false;
	}

	if (buffer[0] == 0x1A && buffer[1] == 0xA2) {
		*tx_counter = (uint16_t)((uint16_t)buffer[2] << 8u) | (uint16_t)buffer[3];
		*rx_counter = (uint16_t)((uint16_t)buffer[4] << 8u) | (uint16_t)buffer[5];
	} else {
		return false;
	}

	return true;
}

static void save_frame_counter(uint16_t tx_counter, uint16_t rx_counter)
{
	uint8_t buffer[6] = {
			0x1A, 0xA2, (uint8_t)(tx_counter >> 8u) & 0xffu, tx_counter & 0xffu, (uint8_t)(rx_counter >> 8u) & 0xffu, rx_counter & 0xffu
	};
	eeprom_write_bytes(&eeprom_handle, 0x20, buffer, sizeof(buffer));
}

static uint32_t adc_get_sample(int num_samples, uint32_t delay_between)
{
	uint32_t adc_value = 0;
	for (int i = 0; i < num_samples; i++) {
		HAL_ADC_Start(&hadc1);
		HAL_ADC_PollForConversion(&hadc1, HAL_MAX_DELAY);
		adc_value += HAL_ADC_GetValue(&hadc1);
		HAL_ADC_Stop(&hadc1);
		HAL_Delay(delay_between);
	}
	return adc_value / num_samples;
}

static float adc_sample_to_photo_diode_current(uint32_t adc_sample, uint32_t amplification)
{
	float voltage = ((float)adc_sample * 3.3f) / 4096.0f  - 97e-3f;
	return voltage / (float)(amplification) * 10e9f;
}

static float read_input_voltage()
{
	ADC_ChannelConfTypeDef config;
	config.Channel = ADC_CHANNEL_5;
	config.Rank = ADC_REGULAR_RANK_1;
	config.SamplingTime = ADC_SAMPLETIME_2CYCLES_5;
	config.SingleDiff = ADC_SINGLE_ENDED;
	config.OffsetNumber = ADC_OFFSET_NONE;
	config.Offset = 0;

	if (HAL_ADC_ConfigChannel(&hadc1, &config) != HAL_OK) {
		Error_Handler();
	}

	return (float)(adc_get_sample(3, 3) * 3.3f) / 4096.0f;
}

static float read_photo_diode_current()
{
	ADC_ChannelConfTypeDef config;
	config.Channel = ADC_CHANNEL_16;
	config.Rank = ADC_REGULAR_RANK_1;
	config.SamplingTime = ADC_SAMPLETIME_2CYCLES_5;
	config.SingleDiff = ADC_SINGLE_ENDED;
	config.OffsetNumber = ADC_OFFSET_NONE;
	config.Offset = 0;

	if (HAL_ADC_ConfigChannel(&hadc1, &config) != HAL_OK) {
		Error_Handler();
	}

	HAL_GPIO_WritePin(PHOTO_ENABLE_GPIO_Port, PHOTO_ENABLE_Pin, GPIO_PIN_SET);

	HAL_GPIO_WritePin(PHOTO_SWITCH_GPIO_Port, PHOTO_SWITCH_Pin, GPIO_PIN_SET);
	HAL_Delay(10);
	uint32_t adc_value_low_amp = adc_get_sample(10, 1);
	uint32_t adc_value_high_amp;
	float photo_diode_current = adc_sample_to_photo_diode_current(adc_value_low_amp, 33000);

	if (adc_value_low_amp < 252) {
		HAL_GPIO_WritePin(PHOTO_SWITCH_GPIO_Port, PHOTO_SWITCH_Pin, GPIO_PIN_RESET);
		HAL_Delay(10);
		adc_value_high_amp = adc_get_sample(20, 1);
		photo_diode_current = adc_sample_to_photo_diode_current(adc_value_high_amp, 1000000);
	}

	if (photo_diode_current < 0) {
		photo_diode_current = 0;
	}

	HAL_GPIO_WritePin(PHOTO_ENABLE_GPIO_Port, PHOTO_ENABLE_Pin, GPIO_PIN_RESET);
	return photo_diode_current;
}

void application_main()
{
	float input_voltage;
	float photo_diode_current;

	bool sht3x_success = false;
	float sht3x_temperature, sht3x_humidity;

	bool mlx90614_success = false;
	float  mlx90614_object_temperature, mlx90614_ambient_temperature;

	HAL_ADCEx_Calibration_Start(&hadc1, ADC_SINGLE_ENDED);
	input_voltage = read_input_voltage();

	// For too low input voltages, return to sleep directly (2 batteries with 1V cutoff)
	if (input_voltage < 2.0f) {
		return;
	}

	uint32_t i2c_enable_tick = HAL_GetTick();
	HAL_GPIO_WritePin(I2C_ENABLE_GPIO_Port, I2C_ENABLE_Pin, GPIO_PIN_RESET);
	HAL_Delay(1);

	rfm95_init(&rfm95_handle);

	// Take photo diode measurements, this takes some time. I2C devices can boot in the meantime.
	photo_diode_current = read_photo_diode_current();

	// SHT3x requires only 1ms to be ready
	uint32_t elapsed_ticks = HAL_GetTick() - i2c_enable_tick;
	if (elapsed_ticks == 0) {
		HAL_Delay(1);
	}

	if (sht3x_init(&sht3x_handle) && sht3x_read_temperature_and_humidity(&sht3x_handle, &sht3x_temperature, &sht3x_humidity)) {
		sht3x_success = true;
	}

	// MLX90614 required around 250ms to be ready
	elapsed_ticks = HAL_GetTick() - i2c_enable_tick;
	if (elapsed_ticks < 300) {
		HAL_Delay(300 - elapsed_ticks);
	}

	if (mlx90614_read_object_temperature(&mlx90614_handle, &mlx90614_object_temperature) &&
		mlx90614_read_ambient_temperature(&mlx90614_handle, &mlx90614_ambient_temperature)) {
		mlx90614_success = true;
	}

	data_packet_t data_packet = {0};
	data_packet.type = CLIMATE_SENSOR;

	if (sht3x_success) {
		data_packet.temperature = (int16_t)roundf(sht3x_temperature * 100);
		data_packet.humidity = (uint16_t)roundf(sht3x_humidity * 100);
	} else {
		data_packet.temperature = INT16_MAX;
		data_packet.humidity = INT16_MAX;
	}

	if (mlx90614_success) {
		data_packet.ir_temperature = (int16_t)roundf(mlx90614_object_temperature * 100);
	} else {
		data_packet.ir_temperature = INT16_MAX;
	}

	data_packet.brightness_current = (uint32_t)photo_diode_current;
	data_packet.battery_voltage = (uint8_t)roundf(input_voltage * 10);

	rfm95_send_data(&rfm95_handle, (uint8_t*)(&data_packet), sizeof(data_packet));

	HAL_GPIO_WritePin(I2C_ENABLE_GPIO_Port, I2C_ENABLE_Pin, GPIO_PIN_SET);
}
