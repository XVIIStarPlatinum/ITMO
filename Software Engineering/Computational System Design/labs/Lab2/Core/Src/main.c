/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body - Кодовый замок через UART
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "usart.h"
#include "gpio.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include <string.h>
#include <ctype.h>
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
#define CODE_LENGTH 8
#define MAX_ATTEMPTS 3
#define TIMEOUT_MS 10000
#define GREEN_LED_TIME 3000
#define RED_BLINK_TIME 5000
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/

/* USER CODE BEGIN PV */
char current_code[CODE_LENGTH + 1] = "12345678";
char input_buffer[CODE_LENGTH + 1] = {0};
char new_code_buffer[CODE_LENGTH + 1] = {0};
uint8_t input_pos = 0;
uint8_t failed_attempts = 0;
uint32_t last_input_time = 0;
uint8_t setting_new_code = 0;
uint8_t new_code_pos = 0;
uint8_t use_interrupts = 0;
uint8_t rx_byte = 0;


volatile uint8_t char_received = 0;
volatile char received_char = 0;
/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
/* USER CODE BEGIN PFP */
void turn_on_green_led(void);
void turn_on_yellow_led(void);
void turn_on_red_led(void);
void turn_off_green_led(void);
void turn_off_yellow_led(void);
void turn_off_red_led(void);
void turn_off_all_leds(void);
void blink_led(GPIO_TypeDef* port, uint16_t pin, uint16_t duration);
void send_string(const char* str);
void reset_input(void);
void check_timeout(void);
void process_char(char c);
void lock_system(void);
void unlock_system(void);

/* Новая вспомогательная функция */
static void wait_ms(uint32_t ms);
/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */

static void wait_ms(uint32_t ms) {
    uint32_t start = HAL_GetTick();
    while ((HAL_GetTick() - start) < ms) {
        /* Ничего не делаем здесь намеренно - позволяем прерываниям срабатывать.
           __NOP() минимально загружает ЦПУ и полезна для отладки. */
        __NOP();
    }
}

void turn_on_green_led(void) {
    HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_SET);
}

void turn_on_yellow_led(void) {
    HAL_GPIO_WritePin(GPIOD, GPIO_PIN_14, GPIO_PIN_SET);
}

void turn_on_red_led(void) {
    HAL_GPIO_WritePin(GPIOD, GPIO_PIN_15, GPIO_PIN_SET);
}

void turn_off_green_led(void) {
    HAL_GPIO_WritePin(GPIOD, GPIO_PIN_13, GPIO_PIN_RESET);
}

void turn_off_yellow_led(void) {
    HAL_GPIO_WritePin(GPIOD, GPIO_PIN_14, GPIO_PIN_RESET);
}

void turn_off_red_led(void) {
    HAL_GPIO_WritePin(GPIOD, GPIO_PIN_15, GPIO_PIN_RESET);
}

void turn_off_all_leds(void) {
    turn_off_green_led();
    turn_off_yellow_led();
    turn_off_red_led();
}

void blink_led(GPIO_TypeDef* port, uint16_t pin, uint16_t duration) {
    HAL_GPIO_WritePin(port, pin, GPIO_PIN_SET);
    wait_ms(duration);
    HAL_GPIO_WritePin(port, pin, GPIO_PIN_RESET);
}

void send_string(const char* str) {
    HAL_UART_Transmit(&huart6, (uint8_t*)str, strlen(str), 1000);
}

void reset_input(void) {
    input_pos = 0;
    memset(input_buffer, 0, sizeof(input_buffer));
    last_input_time = HAL_GetTick();
}

void check_timeout(void) {
    if (input_pos > 0 && (HAL_GetTick() - last_input_time > TIMEOUT_MS)) {
        send_string("Timeout! Code reset.\r\n");
        blink_led(GPIOD, GPIO_PIN_14, 1000);
        reset_input();
    }
}

void lock_system(void) {
    send_string("!!! LOCKED !!! Too many attempts.\r\n");
    uint32_t start = HAL_GetTick();
    while ((HAL_GetTick() - start) < RED_BLINK_TIME) {
        turn_on_red_led();
        wait_ms(250);
        turn_off_red_led();
        wait_ms(250);
    }
    failed_attempts = 0;
    reset_input();
    send_string("System ready. Enter code:\r\n");
}

void unlock_system(void) {
    send_string("\r\n*** UNLOCKED! ***\r\n");
    turn_on_green_led();
    wait_ms(GREEN_LED_TIME);
    turn_off_green_led();
    failed_attempts = 0;
    reset_input();
    send_string("System ready. Enter code:\r\n");
}

void process_char(char c) {
    if (setting_new_code == 1) {
        if (c == '\r' || c == '\n') {
            if (new_code_pos >= CODE_LENGTH) {
                send_string("\r\nNew code entered. Make it active? (y/n): ");
                setting_new_code = 2;
            } else {
                send_string("\r\nCode too short! Need 8 characters.\r\n");
                setting_new_code = 0;
                new_code_pos = 0;
                memset(new_code_buffer, 0, sizeof(new_code_buffer));
            }
            return;
        }

        if (isalnum((unsigned char)c) && new_code_pos < CODE_LENGTH) {
            new_code_buffer[new_code_pos++] = tolower((unsigned char)c);
            HAL_UART_Transmit(&huart6, (uint8_t*)"*", 1, 100);

            if (new_code_pos >= CODE_LENGTH) {
                send_string("\r\nNew code entered. Make it active? (y/n): ");
                setting_new_code = 2;
            }
        }
        return;
    }

    if (setting_new_code == 2) {
        if (c == 'y' || c == 'Y') {
            strcpy(current_code, new_code_buffer);
            send_string("\r\nNew code activated!\r\n");
            blink_led(GPIOD, GPIO_PIN_13, 500);
        } else {
            send_string("\r\nNew code cancelled.\r\n");
        }
        setting_new_code = 0;
        new_code_pos = 0;
        memset(new_code_buffer, 0, sizeof(new_code_buffer));
        reset_input();
        send_string("System ready. Enter code:\r\n");
        return;
    }


    if (c == '+') {
        send_string("\r\nEnter new code (8 chars): ");
        setting_new_code = 1;
        new_code_pos = 0;
        memset(new_code_buffer, 0, sizeof(new_code_buffer));
        return;
    }

    if (isalnum((unsigned char)c)) {
        c = tolower((unsigned char)c);
        input_buffer[input_pos] = c;
        last_input_time = HAL_GetTick();


        if (input_buffer[input_pos] == current_code[input_pos]) {
            blink_led(GPIOD, GPIO_PIN_14, 100);
            HAL_UART_Transmit(&huart6, (uint8_t*)&c, 1, 100);
            input_pos++;
            if (input_pos >= CODE_LENGTH) {
                unlock_system();
            }
        } else {
            send_string("\r\nWrong character!\r\n");
            blink_led(GPIOD, GPIO_PIN_15, 200);
            failed_attempts++;
            reset_input();

            if (failed_attempts >= MAX_ATTEMPTS) {
                lock_system();
            } else {
                send_string("Try again. Enter code:\r\n");
            }
        }
    }
}

void HAL_UART_RxCpltCallback(UART_HandleTypeDef *huart) {
    if (huart->Instance == USART6) {

        received_char = (char)rx_byte;
        char_received = 1;

        HAL_UART_Receive_IT(&huart6, &rx_byte, 1);
    }
}

/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{
  /* USER CODE BEGIN 1 */
  uint8_t button_pressed = 0;
  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_USART6_UART_Init();
  /* USER CODE BEGIN 2 */

  turn_off_all_leds();

  HAL_NVIC_SetPriority(USART6_IRQn, 0, 0);
  HAL_NVIC_EnableIRQ(USART6_IRQn);

  send_string("\r\n");
  send_string("========================================\r\n");
  send_string("    CODE LOCK SYSTEM v2.0 (UART)\r\n");
  send_string("========================================\r\n");
  send_string("Mode: POLLING (press button to toggle)\r\n");
  send_string("System ready. Enter code:\r\n");

  reset_input();

  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
      if (HAL_GPIO_ReadPin(GPIOC, GPIO_PIN_15) == GPIO_PIN_RESET && !button_pressed) {
          wait_ms(50);
          if (HAL_GPIO_ReadPin(GPIOC, GPIO_PIN_15) == GPIO_PIN_RESET) {
              button_pressed = 1;
              use_interrupts = !use_interrupts;

              if (use_interrupts) {
                  send_string("\r\n>>> Mode: INTERRUPTS <<<\r\n");
                  HAL_UART_Receive_IT(&huart6, &rx_byte, 1);
              } else {
                  send_string("\r\n>>> Mode: POLLING <<<\r\n");
                  HAL_UART_AbortReceive_IT(&huart6);
              }

              blink_led(GPIOD, GPIO_PIN_13, 200);
          }
      }

      if (HAL_GPIO_ReadPin(GPIOC, GPIO_PIN_15) == GPIO_PIN_SET) {
          button_pressed = 0;
      }


      if (!use_interrupts) {
          char c;
          if (HAL_OK == HAL_UART_Receive(&huart6, (uint8_t*)&c, 1, 10)) {
              process_char(c);
          }
      } else {
          if (char_received) {
              char_received = 0;
              process_char(received_char);
          }
      }


      check_timeout();

      wait_ms(10);
  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  /** Configure the main internal regulator output voltage
  */
  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE3);

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_NONE;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }

  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_HSI;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV1;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_0) != HAL_OK)
  {
    Error_Handler();
  }
}

/* USER CODE BEGIN 4 */

/* USER CODE END 4 */

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */
  __disable_irq();
  while (1)
  {
  }
  /* USER CODE END Error_Handler_Debug */
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */
