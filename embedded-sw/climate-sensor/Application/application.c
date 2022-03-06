#include "application.h"
#include "data_packet.h"

#include <i2c.h>
#include <spi.h>
#include <adc.h>
#include <math.h>
#include <lptim.h>

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

static void on_after_interrupts_configured();
static bool reload_config(rfm95_eeprom_config_t *config);
static void save_config(const rfm95_eeprom_config_t *config);
static uint32_t get_precision_tick();
static void precision_sleep_until(uint32_t target_ticks);
static uint8_t random_int(uint8_t max);
static uint8_t get_battery_level();

static uint8_t battery_level = 0xff;

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
		.device_address = TTN_KEYS_DEVICE_ADDRESS,
		.application_session_key = TTN_KEYS_APPLICATION_SESSION_KEY,
		.network_session_key = TTN_KEYS_NETWORK_SESSION_KEY,
		.precision_tick_frequency = 32000,
		.precision_tick_drift_ns_per_s = 100000,
		.receive_mode = RFM95_RECEIVE_MODE_RX1_ONLY,
		.get_precision_tick = get_precision_tick,
		.precision_sleep_until = precision_sleep_until,
		.random_int = random_int,
		.get_battery_level = get_battery_level,
		.reload_config = reload_config,
		.save_config = save_config,
		.on_after_interrupts_configured = on_after_interrupts_configured
};

eeprom_handle_t eeprom_handle = {
		.i2c_handle = &hi2c1,
		.device_address = EEPROM_24LC32A_ADDRESS,
		.max_address = EEPROM_24LC32A_MAX_ADDRESS,
		.page_size = EEPROM_24LC32A_PAGE_SIZE
};

#ifdef CLIMATE_SENSOR_INCLUDE_MLX90614
mlx90614_handle_t mlx90614_handle = {
		.i2c_handle = &hi2c1,
		.device_address = MLX90614_DEFAULT_ADDRESS
};
#endif

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

static float adc_value_to_photo_diode_current(uint32_t adc_sample, uint32_t amplification)
{
	float voltage = ((float)adc_sample * 3.3f) / 4096.0f  - 97e-3f;
	return voltage / (float)(amplification) * 10e9f;
}

static float adc_read_photo_diode_current()
{
	HAL_GPIO_WritePin(PHOTO_ENABLE_GPIO_Port, PHOTO_ENABLE_Pin, GPIO_PIN_SET);
	HAL_GPIO_WritePin(PHOTO_SWITCH_GPIO_Port, PHOTO_SWITCH_Pin, GPIO_PIN_SET);
	HAL_Delay(10);

	uint32_t adc_value_low_amp = adc_read(ADC_CHANNEL_16, ADC_SAMPLETIME_247CYCLES_5);
	uint32_t adc_value_high_amp;
	float photo_diode_current;

	if (adc_value_low_amp < 252) {
		HAL_GPIO_WritePin(PHOTO_SWITCH_GPIO_Port, PHOTO_SWITCH_Pin, GPIO_PIN_RESET);
		HAL_Delay(10);
		adc_value_high_amp = adc_read(ADC_CHANNEL_16, ADC_SAMPLETIME_247CYCLES_5);

		photo_diode_current = adc_value_to_photo_diode_current(adc_value_high_amp, 1000000);
	} else {
		photo_diode_current = adc_value_to_photo_diode_current(adc_value_low_amp, 33000);
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

#ifdef CLIMATE_SENSOR_INCLUDE_MLX90614
	bool mlx90614_success = false;
	float  mlx90614_object_temperature, mlx90614_ambient_temperature;
#endif

	uint32_t start_ticks = get_precision_tick();

	// RFM95 module draws a lot of power so immediately init and put it to sleep after power up
	rfm95_init(&rfm95_handle);

	// Wait one second for input voltage measurement
	precision_sleep_until(get_precision_tick() + rfm95_handle.precision_tick_frequency);

	HAL_ADCEx_Calibration_Start(&hadc1, ADC_SINGLE_ENDED);

	input_voltage = adc_read_input_voltage();

	// Immediately return if input voltage is too low
	if (input_voltage < 2.0f) {
		return;
	}

	// Take photo diode measurements, this takes some time.
	photo_diode_current = adc_read_photo_diode_current();

	if (sht3x_init(&sht3x_handle) && sht3x_read_temperature_and_humidity(&sht3x_handle, &sht3x_temperature, &sht3x_humidity)) {
		sht3x_success = true;
	}

#ifdef CLIMATE_SENSOR_INCLUDE_MLX90614

	// Wait at least 300ms for MLX to be ready
	precision_sleep_until(start_ticks + rfm95_handle.precision_tick_frequency * 3 / 10);

	mlx90614_configure_emissivity(&mlx90614_handle, 0.95f);

	if (mlx90614_read_object_temperature(&mlx90614_handle, &mlx90614_object_temperature) &&
	        mlx90614_read_ambient_temperature(&mlx90614_handle, &mlx90614_ambient_temperature)) {
		mlx90614_success = true;
	}

	mlx90614_sleep(&mlx90614_handle);
#endif

	// Save input voltage in global variable used by rfm95 callback
	battery_level = (uint8_t)((input_voltage - 2.0f) * 254.0f / 1.3f);

	data_packet_t data_packet = {0};
	data_packet.type = CLIMATE_SENSOR;

	if (sht3x_success) {
		data_packet.temperature = (int16_t)roundf(sht3x_temperature * 100);
		data_packet.humidity = (uint16_t)roundf(sht3x_humidity * 100);
	} else {
		data_packet.temperature = INT16_MAX;
		data_packet.humidity = INT16_MAX;
	}

#ifdef CLIMATE_SENSOR_INCLUDE_MLX90614
	if (mlx90614_success) {
		data_packet.ir_temperature = (int16_t)roundf(mlx90614_object_temperature * 100);
	} else {
		data_packet.ir_temperature = INT16_MAX;
	}
#else
	data_packet.ir_temperature = INT16_MAX;
#endif

	data_packet.brightness_current = (uint32_t)photo_diode_current;
	data_packet.battery_voltage = (uint8_t)roundf(input_voltage * 10);

	rfm95_send_receive_cycle(&rfm95_handle, (uint8_t*)(&data_packet), 12);
}

void on_after_interrupts_configured()
{
	HAL_NVIC_EnableIRQ(EXTI0_IRQn);
	HAL_NVIC_EnableIRQ(EXTI2_IRQn);
}

void HAL_GPIO_EXTI_Callback(uint16_t GPIO_Pin)
{
	if (GPIO_Pin == RFM95_DIO0_Pin) {
		rfm95_on_interrupt(&rfm95_handle, RFM95_INTERRUPT_DIO0);
	} else if (GPIO_Pin == RFM95_DIO5_Pin) {
		rfm95_on_interrupt(&rfm95_handle, RFM95_INTERRUPT_DIO5);
	}
}

volatile uint32_t lptim_tick_msb = 0;

static uint32_t get_precision_tick()
{
	__disable_irq();
	uint32_t precision_tick = lptim_tick_msb | HAL_LPTIM_ReadCounter(&hlptim1);
	__enable_irq();
	return precision_tick;
}

void HAL_LPTIM_AutoReloadMatchCallback(LPTIM_HandleTypeDef *hlptim)
{
	lptim_tick_msb += 0x10000;
}

static void precision_sleep_until(uint32_t target_ticks)
{
	while (true) {

		uint32_t start_ticks = get_precision_tick();
		if (start_ticks > target_ticks) {
			break;
		}

		uint32_t ticks_to_sleep = target_ticks - start_ticks;

		// Only use sleep for at least 10 ticks.
		if (ticks_to_sleep >= 10) {

			// Calculate required value of compare register for the sleep minus a small buffer time to compensate
			// for any ticks that occur while we perform this calculation.
			uint32_t compare = (start_ticks & 0xffff) + ticks_to_sleep - 2;

			// If the counter auto-reloads we will be woken up anyway.
			if (compare > 0xffff) {
				HAL_SuspendTick();
				HAL_PWREx_EnterSTOP2Mode(PWR_STOPENTRY_WFI);
				HAL_ResumeTick();

				// Otherwise, set compare register and use the compare match interrupt to wake up in time.
			} else {
				__HAL_LPTIM_COMPARE_SET(&hlptim1, compare);
				while (!__HAL_LPTIM_GET_FLAG(&hlptim1, LPTIM_FLAG_CMPOK)); // TODO: Timeout?
				__HAL_LPTIM_CLEAR_FLAG(&hlptim1, LPTIM_FLAG_CMPM);
				__HAL_LPTIM_ENABLE_IT(&hlptim1, LPTIM_IT_CMPM);
				HAL_SuspendTick();
				HAL_PWREx_EnterSTOP2Mode(PWR_STOPENTRY_WFI);
				HAL_ResumeTick();
				__HAL_LPTIM_DISABLE_IT(&hlptim1, LPTIM_IT_CMPM);
			}
		} else {
			break;
		}
	}

	// Busy wait until we have reached the target.
	while (get_precision_tick() < target_ticks);
}

static uint8_t random_int(uint8_t max)
{
	return adc_read(ADC_CHANNEL_5, ADC_SAMPLETIME_247CYCLES_5) % max;
}

static uint8_t get_battery_level()
{
	return battery_level;
}

static bool reload_config(rfm95_eeprom_config_t *config)
{
	return eeprom_read_bytes(&eeprom_handle, 0x000, (uint8_t*)config, sizeof(rfm95_eeprom_config_t));
}

static void save_config(const rfm95_eeprom_config_t *config)
{
	eeprom_write_bytes(&eeprom_handle, 0x000, (uint8_t*)config, sizeof(rfm95_eeprom_config_t));
}
