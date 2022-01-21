#include "main.h"
#include "rfm95.h"
#include "eeprom.h"
#include "i2c.h"
#include "spi.h"
#include "tsc.h"
#include "adc.h"
#include "math.h"
#include "data_packet.h"

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

#define FRAME_COUNTER_MAGIC_UPPER_BYTE 0x11
#define FRAME_COUNTER_MAGIC_LOWER_BYTE 0x99

static bool reload_frame_counter(uint16_t *tx_counter, uint16_t *rx_counter);
static void save_frame_counter(uint16_t tx_counter, uint16_t rx_counter);

rfm95_handle_t rfm95_handle = {
	.spi_handle = &hspi1,
	.nss_port = RFM95_NSS_GPIO_Port,
	.nss_pin = RFM95_NSS_Pin,
	.nrst_port = RFM95_NRST_GPIO_Port,
	.nrst_pin = RFM95_NRST_Pin,
	.irq_port = RFM95_IRQ_GPIO_Port,
	.irq_pin = RFM95_IRQ_Pin,
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
	.device_address = EEPROM_CAV24C04_ADDRESS,
	.max_address = EEPROM_CAV24C04_MAX_ADDRESS,
	.page_size = EEPROM_CAV24C04_PAGE_SIZE,
	.addressing_type = EEPROM_CAV24C04_ADDRESSING_TYPE
};

static bool reload_frame_counter(uint16_t *tx_counter, uint16_t *rx_counter)
{
	uint8_t buffer[6];

	if (!eeprom_read_bytes(&eeprom_handle, 0x00, buffer, sizeof(buffer))) {
		return false;
	}

	if (buffer[0] == FRAME_COUNTER_MAGIC_UPPER_BYTE && buffer[1] == FRAME_COUNTER_MAGIC_LOWER_BYTE) {
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
		FRAME_COUNTER_MAGIC_UPPER_BYTE,
		FRAME_COUNTER_MAGIC_LOWER_BYTE,
		(uint8_t)(tx_counter >> 8u) & 0xffu, tx_counter & 0xffu,
		(uint8_t)(rx_counter >> 8u) & 0xffu, rx_counter & 0xffu
	};
	eeprom_write_bytes(&eeprom_handle, 0x00, buffer, sizeof(buffer));
}

static uint32_t adc_read(uint32_t channel, uint32_t sampling_time)
{
	ADC_ChannelConfTypeDef config;
	config.Channel = channel;
	config.Rank = ADC_REGULAR_RANK_1;
	config.SamplingTime = sampling_time;
	config.SingleDiff = ADC_SINGLE_ENDED;
	config.OffsetNumber = ADC_OFFSET_NONE;
	config.Offset = 0;

	if (HAL_ADC_ConfigChannel(&hadc1, &config) != HAL_OK) {
		return UINT32_MAX;
	}

	if (HAL_ADC_Start(&hadc1) != HAL_OK) {
		return UINT32_MAX;
	}

	uint32_t value;

	if (HAL_ADC_PollForConversion(&hadc1, HAL_MAX_DELAY) == HAL_OK) {
		value = HAL_ADC_GetValue(&hadc1);
	} else {
		value = UINT32_MAX;
	}

	HAL_ADC_Stop(&hadc1);
	return value;
}

static float adc_read_input_voltage()
{
	uint32_t value = adc_read(ADC_CHANNEL_5, ADC_SAMPLETIME_247CYCLES_5);
	return (float)value * 3.3f / 4096.0f;
}

static float adc_read_internal_temperature()
{
	uint32_t value = adc_read(ADC_CHANNEL_TEMPSENSOR, ADC_SAMPLETIME_640CYCLES_5);
	return (float)__HAL_ADC_CALC_TEMPERATURE(3300, value, ADC_RESOLUTION_12B);
}

static uint32_t tsc_read_value()
{
	HAL_TSC_IODischarge(&htsc, ENABLE);
	HAL_Delay(1);

	if (HAL_TSC_Start(&htsc) != HAL_OK) {
		return UINT32_MAX;
	}

	HAL_TSC_PollForAcquisition(&htsc);

	uint32_t value;

	if (HAL_TSC_GroupGetStatus(&htsc, TSC_GROUP2_IDX) == TSC_GROUP_COMPLETED) {
		value = HAL_TSC_GroupGetValue(&htsc, TSC_GROUP2_IDX);
	} else {
		value = UINT32_MAX;
	}

	HAL_TSC_Stop(&htsc);
	return value;
}

void application_main()
{
	// Turn on I2C Bus and wait for EEPROM to be ready
	HAL_GPIO_WritePin(I2C_ENABLE_GPIO_Port, I2C_ENABLE_Pin, GPIO_PIN_RESET);
	HAL_Delay(10);

	// RFM95 module draws a lot of power so immediately init and put it to sleep after power up
	rfm95_init(&rfm95_handle);

	while (true) {
		uint32_t moisture_counter = tsc_read_value();
		HAL_Delay(1000 + moisture_counter / 5);
	}

	// Input voltage is only accurate after 1s?
	/*uint32_t tick = HAL_GetTick();
	if (tick < 1000) {
		HAL_Delay(1000 - tick);
	}

	HAL_ADCEx_Calibration_Start(&hadc1, ADC_SINGLE_ENDED);

	float temperature = adc_read_internal_temperature();
	uint32_t moisture_counter = tsc_read_value();

	float input_voltage = adc_read_input_voltage();

	// Immediately return if input voltage is too low
	if (input_voltage < 2.0f) {
		return;
	}

	data_packet_t data_packet = { 0 };
	data_packet.type = IRRIGATION_SENSOR;
	data_packet.battery_voltage = (uint8_t)roundf(input_voltage * 10);
	data_packet.temperature = (int16_t)roundf(temperature * 100);
	data_packet.moisture_counter = moisture_counter;

	rfm95_send_data(&rfm95_handle, (uint8_t*)(&data_packet), 8);*/

	HAL_GPIO_WritePin(I2C_ENABLE_GPIO_Port, I2C_ENABLE_Pin, GPIO_PIN_SET);
}
