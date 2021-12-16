#include "main.h"
#include "rfm95.h"
#include "eeprom.h"
#include "i2c.h"
#include "spi.h"

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
	.device_address = EEPROM_24LC32A_ADDRESS,
	.max_address = EEPROM_24LC32A_MAX_ADDRESS,
	.page_size = EEPROM_24LC32A_PAGE_SIZE
};

static bool reload_frame_counter(uint16_t *tx_counter, uint16_t *rx_counter)
{
	uint8_t buffer[6];

	if (!eeprom_read_bytes(&eeprom_handle, 0x20, buffer, sizeof(buffer))) {
		return false;
	}

	if (buffer[0] == 0x1A && buffer[1] == 0xA1) {
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
		0x1A,
		0xA1,
		(uint8_t)(tx_counter >> 8u) & 0xffu, tx_counter & 0xffu,
		(uint8_t)(rx_counter >> 8u) & 0xffu, rx_counter & 0xffu
	};
	eeprom_write_bytes(&eeprom_handle, 0x20, buffer, sizeof(buffer));
}

typedef struct
{
	uint8_t sensor_type;
	int16_t temperature;
	uint16_t humidity;
	union
	{
		struct
		{
			int16_t ir_temperature;
			uint32_t brightness_current;
		};
		struct
		{
			uint8_t moisture;
		};
	};
	uint8_t battery_voltage;
} __packed data_packet_t;

void application_main()
{
	data_packet_t data_packet = {0};
	rfm95_send_data(&rfm95_handle, (uint8_t*)(&data_packet), sizeof(data_packet));
}
