/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.h
  * @brief          : Header for main.c file.
  *                   This file contains the common defines of the application.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; Copyright (c) 2020 STMicroelectronics.
  * All rights reserved.</center></h2>
  *
  * This software component is licensed by ST under BSD 3-Clause license,
  * the "License"; You may not use this file except in compliance with the
  * License. You may obtain a copy of the License at:
  *                        opensource.org/licenses/BSD-3-Clause
  *
  ******************************************************************************
  */
/* USER CODE END Header */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __MAIN_H
#define __MAIN_H

#ifdef __cplusplus
extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32l4xx_hal.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

/* Exported types ------------------------------------------------------------*/
/* USER CODE BEGIN ET */

/* USER CODE END ET */

/* Exported constants --------------------------------------------------------*/
/* USER CODE BEGIN EC */

/* USER CODE END EC */

/* Exported macro ------------------------------------------------------------*/
/* USER CODE BEGIN EM */

/* USER CODE END EM */

/* Exported functions prototypes ---------------------------------------------*/
void Error_Handler(void);

/* USER CODE BEGIN EFP */

/* USER CODE END EFP */

/* Private defines -----------------------------------------------------------*/
#define VIN_SENSE_Pin GPIO_PIN_0
#define VIN_SENSE_GPIO_Port GPIOA
#define RFM95_DIO5_Pin GPIO_PIN_2
#define RFM95_DIO5_GPIO_Port GPIOA
#define RFM95_DIO5_EXTI_IRQn EXTI2_IRQn
#define RFM95_NRST_Pin GPIO_PIN_3
#define RFM95_NRST_GPIO_Port GPIOA
#define RFM95_NSS_Pin GPIO_PIN_4
#define RFM95_NSS_GPIO_Port GPIOA
#define RFM95_DIO0_Pin GPIO_PIN_0
#define RFM95_DIO0_GPIO_Port GPIOB
#define RFM95_DIO0_EXTI_IRQn EXTI0_IRQn
#define PHOTO_SENSE_Pin GPIO_PIN_1
#define PHOTO_SENSE_GPIO_Port GPIOB
#define PHOTO_ENABLE_Pin GPIO_PIN_11
#define PHOTO_ENABLE_GPIO_Port GPIOA
#define PHOTO_SWITCH_Pin GPIO_PIN_12
#define PHOTO_SWITCH_GPIO_Port GPIOA
#define SWDIO_Pin GPIO_PIN_13
#define SWDIO_GPIO_Port GPIOA
#define SWCLK_Pin GPIO_PIN_14
#define SWCLK_GPIO_Port GPIOA
#define I2C_ENABLE_Pin GPIO_PIN_5
#define I2C_ENABLE_GPIO_Port GPIOB
/* USER CODE BEGIN Private defines */

/* USER CODE END Private defines */

#ifdef __cplusplus
}
#endif

#endif /* __MAIN_H */
