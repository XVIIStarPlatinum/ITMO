/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2025 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "i2c.h"
#include "tim.h"
#include "usart.h"
#include "gpio.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include <string.h>
#include <stdio.h>
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
#define NUM_PULSES 9
#define SEQUENCE_LENGTH 27

// Ча�?тоты звуков (Гц) - легко различимые
const uint16_t FREQUENCIES[NUM_PULSES] = {
    262, 294, 330, 349, 392, 440, 494, 523, 587  // До, Ре, Ми, Фа, Соль, Л�?, Си, До2, Ре2
};

typedef struct {
    uint8_t green;
    uint8_t yellow;
    uint8_t red;
    uint16_t freq;
    char symbol;
} Pulse_t;

const Pulse_t PULSES[NUM_PULSES] = {
    {20,  0,   0,  262, '1'},  // Зеленый 20%
    {50,  0,   0,  294, '2'},  // Зеленый 50%
    {100, 0,   0,  330, '3'},  // Зеленый 100%
    {0,   20,  0,  349, '4'},  // Желтый 20%
    {0,   50,  0,  392, '5'},  // Желтый 50%
    {0,   100, 0,  440, '6'},  // Желтый 100%
    {0,   0,   20, 494, '7'},  // Кра�?ный 20%
    {0,   0,   50, 523, '8'},  // Кра�?ный 50%
    {0,   0,   100, 587, '9'}  // Кра�?ный 100%
};

typedef enum {
    SPEED_SLOW = 0,
    SPEED_MEDIUM = 1,
    SPEED_FAST = 2
} Speed_t;

const uint16_t SPEED_DURATIONS[3] = {1000, 600, 300};

typedef enum {
    MODE_BOTH = 0,
    MODE_LED_ONLY = 1,
    MODE_SOUND_ONLY = 2
} PlayMode_t;

typedef enum {
    GAME_IDLE = 0,
    GAME_COUNTDOWN = 1,
    GAME_PLAYING = 2,
    GAME_FINISHED = 3
} GameState_t;

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/

/* USER CODE BEGIN PV */
extern TIM_HandleTypeDef htim1;
extern TIM_HandleTypeDef htim4;
extern TIM_HandleTypeDef htim6;
extern UART_HandleTypeDef huart6;

static Speed_t current_speed = SPEED_SLOW;
static PlayMode_t play_mode = MODE_BOTH;
static GameState_t game_state = GAME_IDLE;

static uint8_t sequence[SEQUENCE_LENGTH];
static uint8_t sequence_pos = 0;
static uint32_t score = 0;
static uint8_t mistakes = 0;
static uint8_t input_received = 0;
static char trace[SEQUENCE_LENGTH];

static uint32_t countdown_start = 0;
static uint32_t pulse_start = 0;

uint8_t uart_rx_buffer[128];
volatile uint16_t uart_rx_head = 0;
volatile uint16_t uart_rx_tail = 0;
uint8_t uart_rx_byte;

volatile uint32_t uart_tx_head = 0;
volatile uint32_t uart_tx_tail = 0;
uint8_t uart_tx_buffer[256];
volatile uint8_t uart_tx_busy = 0;
/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
/* USER CODE BEGIN PFP */
static void SetPWM(uint8_t green, uint8_t yellow, uint8_t red);
static void SetBuzzer(uint16_t freq);
static void StopBuzzer(void);
static void PlayPulse(uint8_t pulse_idx);
static void StopPulse(void);
static void GenerateSequence(void);
static void StartGame(void);
static void StopGame(void);
static void ProcessInput(char c);
static void PrintGameStats(void);
static void UART_SendString(const char* str);
/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */

static void SetPWM(uint8_t green, uint8_t yellow, uint8_t red) {
    if (play_mode == MODE_SOUND_ONLY) {
        __HAL_TIM_SET_COMPARE(&htim4, TIM_CHANNEL_2, 0);
        __HAL_TIM_SET_COMPARE(&htim4, TIM_CHANNEL_3, 0);
        __HAL_TIM_SET_COMPARE(&htim4, TIM_CHANNEL_4, 0);
        return;
    }

    __HAL_TIM_SET_COMPARE(&htim4, TIM_CHANNEL_2, (green * 2000) / 100);
    __HAL_TIM_SET_COMPARE(&htim4, TIM_CHANNEL_3, (yellow * 2000) / 100);
    __HAL_TIM_SET_COMPARE(&htim4, TIM_CHANNEL_4, (red * 2000) / 100);
}

static void SetBuzzer(uint16_t freq) {
    if (play_mode == MODE_LED_ONLY || freq == 0) {
        HAL_TIM_PWM_Stop(&htim1, TIM_CHANNEL_1);
        return;
    }


    uint32_t timer_clock = 144000000;
    uint16_t prescaler = (timer_clock / 1000000) - 1;
    uint32_t arr = (1000000 / freq) - 1;

    if (arr > 65535) arr = 65535;
    if (arr < 10) arr = 10;

    char debug_buf[64];
    snprintf(debug_buf, sizeof(debug_buf), "[PSC=%u, ARR=%lu, CCR=%lu] ", prescaler, arr, arr/2);
    UART_SendString(debug_buf);

    HAL_TIM_PWM_Stop(&htim1, TIM_CHANNEL_1);

    htim1.Instance->PSC = prescaler;
    htim1.Instance->ARR = arr;
    htim1.Instance->CCR1 = arr / 2;
    htim1.Instance->CNT = 0;

    htim1.Instance->EGR = TIM_EGR_UG;

    HAL_TIM_PWM_Start(&htim1, TIM_CHANNEL_1);
}

static void StopBuzzer(void) {
    HAL_TIM_PWM_Stop(&htim1, TIM_CHANNEL_1);
}

static void PlayPulse(uint8_t pulse_idx) {
    const Pulse_t* p = &PULSES[pulse_idx];
    SetPWM(p->green, p->yellow, p->red);
    SetBuzzer(p->freq);
}

static void StopPulse(void) {
    SetPWM(0, 0, 0);
    StopBuzzer();
}

static void GenerateSequence(void) {
    for (int i = 0; i < SEQUENCE_LENGTH; i++) {
        sequence[i] = i % NUM_PULSES;
    }
}

static void StartGame(void) {
    game_state = GAME_COUNTDOWN;
    countdown_start = HAL_GetTick();
    score = 0;
    mistakes = 0;
    sequence_pos = 0;
    memset(trace, 0, sizeof(trace));
    GenerateSequence();

    UART_SendString("\r\n=== GAME STARTING IN 3 SECONDS ===\r\n");
}

static void StopGame(void) {
    StopPulse();
    HAL_TIM_Base_Stop_IT(&htim6);
    game_state = GAME_FINISHED;
    PrintGameStats();
}

static void PrintGameStats(void) {
    char buf[256];
    snprintf(buf, sizeof(buf),
             "\r\n=== GAME FINISHED ===\r\n"
             "Score: %lu\r\n"
             "Correct: %d / %d\r\n"
             "Trace: ",
             score, SEQUENCE_LENGTH - mistakes, SEQUENCE_LENGTH);
    UART_SendString(buf);

    for (int i = 0; i < SEQUENCE_LENGTH; i++) {
        if (trace[i] == '+') {
            UART_SendString("[OK]");
        } else if (trace[i] == '-') {
            UART_SendString("[MISS]");
        } else {
            UART_SendString("[TIMEOUT]");
        }
    }
    UART_SendString("\r\n");
    game_state = GAME_IDLE;
}

static void ProcessInput(char c) {
    if (game_state == GAME_IDLE) {
        for (int i = 0; i < NUM_PULSES; i++) {
            if (c == PULSES[i].symbol) {
                PlayPulse(i);

                char buf[128];
                snprintf(buf, sizeof(buf),
                         "\r\nPulse '%c': Color=%s, Brightness=%d%%, Frequency=%d Hz\r\n",
                         c,
                         PULSES[i].green ? "Green" : (PULSES[i].yellow ? "Yellow" : "Red"),
                         PULSES[i].green + PULSES[i].yellow + PULSES[i].red,
                         PULSES[i].freq);
                UART_SendString(buf);
                return;
            }
        }

        if (c == '+') {
            current_speed = (current_speed + 1) % 3;
            const char* speed_names[] = {"SLOW", "MEDIUM", "FAST"};
            char buf[64];
            snprintf(buf, sizeof(buf), "\r\nSpeed: %s (%d ms)\r\n",
                     speed_names[current_speed],
                     SPEED_DURATIONS[current_speed]);
            UART_SendString(buf);
        } else if (c == 'a' || c == 'A') {
            play_mode = (play_mode + 1) % 3;
            const char* mode_names[] = {"LED + SOUND", "LED ONLY", "SOUND ONLY"};
            char buf[64];
            snprintf(buf, sizeof(buf), "\r\nPlay mode: %s\r\n", mode_names[play_mode]);
            UART_SendString(buf);
        } else if (c == '\r' || c == '\n') {
            StartGame();
        }
    } else if (game_state == GAME_PLAYING) {
        if (input_received) return;

        uint8_t expected = sequence[sequence_pos];
        if (c == PULSES[expected].symbol) {
            trace[sequence_pos] = '+';
            score += 10 * (current_speed + 1);
        } else {
            trace[sequence_pos] = '-';
            mistakes++;
        }
        input_received = 1;
    } else if (c == '\r' || c == '\n') {
        if (game_state == GAME_COUNTDOWN || game_state == GAME_PLAYING) {
            UART_SendString("\r\n=== GAME STOPPED ===\r\n");
            StopGame();
        }
    }
}

static void UART_SendString(const char* str) {
    HAL_UART_Transmit(&huart6, (uint8_t*)str, strlen(str), HAL_MAX_DELAY);
}

/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{
  /* USER CODE BEGIN 1 */

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
  MX_TIM1_Init();
  MX_TIM6_Init();
  MX_USART6_UART_Init();
  MX_I2C1_Init();
  MX_TIM4_Init();
  /* USER CODE BEGIN 2 */

  UART_SendString("\r\n\r\n=== RHYTHM GAME ===\r\n");
  UART_SendString("System initialized successfully!\r\n");

  char freq_buf[128];
  snprintf(freq_buf, sizeof(freq_buf), "SYSCLK: %lu Hz\r\n", HAL_RCC_GetSysClockFreq());
  UART_SendString(freq_buf);
  snprintf(freq_buf, sizeof(freq_buf), "HCLK: %lu Hz\r\n", HAL_RCC_GetHCLKFreq());
  UART_SendString(freq_buf);
  snprintf(freq_buf, sizeof(freq_buf), "PCLK1: %lu Hz\r\n", HAL_RCC_GetPCLK1Freq());
  UART_SendString(freq_buf);
  snprintf(freq_buf, sizeof(freq_buf), "PCLK2: %lu Hz\r\n\r\n", HAL_RCC_GetPCLK2Freq());
  UART_SendString(freq_buf);

  UART_SendString("=== TESTING BUZZER ===\r\n");

  uint32_t test_arr = (144000000 / 440) - 1;
  __HAL_TIM_SET_AUTORELOAD(&htim1, test_arr);
  __HAL_TIM_SET_COMPARE(&htim1, TIM_CHANNEL_1, test_arr / 2);
  HAL_TIM_PWM_Start(&htim1, TIM_CHANNEL_1);
  HAL_Delay(500);
  HAL_TIM_PWM_Stop(&htim1, TIM_CHANNEL_1);

  HAL_Delay(200);

  test_arr = (144000000 / 523) - 1;
  __HAL_TIM_SET_AUTORELOAD(&htim1, test_arr);
  __HAL_TIM_SET_COMPARE(&htim1, TIM_CHANNEL_1, test_arr / 2);
  HAL_TIM_PWM_Start(&htim1, TIM_CHANNEL_1);
  HAL_Delay(500);
  HAL_TIM_PWM_Stop(&htim1, TIM_CHANNEL_1);

  UART_SendString("Buzzer test complete!\r\n\r\n");

  HAL_TIM_PWM_Start(&htim4, TIM_CHANNEL_2);
  HAL_TIM_PWM_Start(&htim4, TIM_CHANNEL_3);
  HAL_TIM_PWM_Start(&htim4, TIM_CHANNEL_4);

  SetPWM(0, 0, 0);

  HAL_UART_Receive_IT(&huart6, &uart_rx_byte, 1);

  UART_SendString("Commands:\r\n");
  UART_SendString("  1-9 : Test pulses (color + sound)\r\n");
  UART_SendString("  +   : Change speed (SLOW/MEDIUM/FAST)\r\n");
  UART_SendString("  a   : Change mode (LED+SOUND/LED ONLY/SOUND ONLY)\r\n");
  UART_SendString("  Enter: Start/Stop game\r\n");
  UART_SendString("\r\nReady! Press 1-9 to test...\r\n");

  /* USER CODE END 2 */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    uint32_t now = HAL_GetTick();

    if (game_state == GAME_COUNTDOWN) {
        if (now - countdown_start >= 3000) {
            game_state = GAME_PLAYING;
            sequence_pos = 0;
            pulse_start = now;
            input_received = 0;
            PlayPulse(sequence[0]);

            __HAL_TIM_SET_COUNTER(&htim6, 0);
            __HAL_TIM_SET_AUTORELOAD(&htim6, SPEED_DURATIONS[current_speed] - 1);
            HAL_TIM_Base_Start_IT(&htim6);

            UART_SendString("=== GO! ===\r\n");
        }
    }

    // Чтение UART
    if (uart_rx_head != uart_rx_tail) {
        char c = uart_rx_buffer[uart_rx_tail];
        uart_rx_tail = (uart_rx_tail + 1) % 128;
        ProcessInput(c);
    }
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
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
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLM = 16;
  RCC_OscInitStruct.PLL.PLLN = 240;
  RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV4;
  RCC_OscInitStruct.PLL.PLLQ = 4;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }

  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV4;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV2;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_1) != HAL_OK)
  {
    Error_Handler();
  }
}

/* USER CODE BEGIN 4 */

void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim) {
    if (htim->Instance == TIM6 && game_state == GAME_PLAYING) {
        StopPulse();

        if (!input_received) {
            trace[sequence_pos] = '0';
            mistakes++;
        }

        sequence_pos++;

        if (sequence_pos >= SEQUENCE_LENGTH) {
            StopGame();
        } else {
            input_received = 0;
            PlayPulse(sequence[sequence_pos]);
        }
    }
}

// UART RX callback
void HAL_UART_RxCpltCallback(UART_HandleTypeDef *huart) {
    if (huart->Instance == USART6) {
        uint16_t next = (uart_rx_head + 1) % 128;
        if (next != uart_rx_tail) {
            uart_rx_buffer[uart_rx_head] = uart_rx_byte;
            uart_rx_head = next;
        }
        HAL_UART_Receive_IT(&huart6, &uart_rx_byte, 1);
    }
}

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
