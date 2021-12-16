#pragma once

#include <sht3x.h>
#include <rfm95.h>
#include <eeprom.h>
#include <mlx90614.h>

extern sht3x_handle_t sht3x_handle;
extern rfm95_handle_t rfm95_handle;
extern eeprom_handle_t eeprom_handle;
extern mlx90614_handle_t mlx90614_handle;

/**
 * Application main called by the STM32Cube init code.
 */
void application_main();