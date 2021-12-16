#pragma once

#include <rfm95.h>
#include <eeprom.h>

extern rfm95_handle_t rfm95_handle;
extern eeprom_handle_t eeprom_handle;

/**
 * Application main called by the STM32Cube init code.
 */
void application_main();
