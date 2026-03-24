#include "lcd.h"

#include <string.h>


#define LCD_RST_I2C_ADDRESS 0xE0
#define LCD_RST_I2C_WRITE_ADDRESS ((LCD_RST_I2C_ADDRESS) & ~1)
#define LCD_RST_OUTPUT_REG (0x1)
#define LCD_RST_CONFIG_REG (0x3)

#define LCD_I2C_ADDRESS 0x78
#define LCD_I2C_WRITE_ADDRESS ((LCD_I2C_ADDRESS) & ~1)
#define LCD_I2C_COMMAND_CONTROL_BYTE 0x00
#define LCD_I2C_DATA_CONTROL_BYTE 0x40

#define LCD_MAX_BYTES_PER_STEP 192
#define LCD_BUF_SIZE 1024


static uint8_t buf_back[LCD_BUF_SIZE];
static uint8_t buf_front[LCD_BUF_SIZE];


static inline size_t min(size_t a, size_t b) {
    return a < b ? a : b;
}

static inline size_t ceil(size_t n, size_t d) {
    return n / d + (n % d != 0);
}

static void lcd_reset(I2C_HandleTypeDef * i2c) {
    uint8_t rstConfig = 0xFE;
    uint8_t rstOutputOn = 0x01;
    uint8_t rstOutputOff = 0x00;

    HAL_I2C_Mem_Write(i2c, LCD_RST_I2C_WRITE_ADDRESS, LCD_RST_CONFIG_REG, 1, &rstConfig, 1, 100);
    HAL_I2C_Mem_Write(i2c, LCD_RST_I2C_WRITE_ADDRESS, LCD_RST_OUTPUT_REG, 1, &rstOutputOff, 1, 100);
    HAL_I2C_Mem_Write(i2c, LCD_RST_I2C_WRITE_ADDRESS, LCD_RST_OUTPUT_REG, 1, &rstOutputOn, 1, 100);
    HAL_Delay(1);
}

void lcd_init(I2C_HandleTypeDef * i2c) {
    static uint8_t initCommands[] = {
            0x20, 0x00, // horizontal mode
            0x81, 0xFF, // high contrast (255)
            0xC8,       // re-mapped mode
            0xA1,       // segment re-map
            0xD5, 0xF0, // set display clock
            0x8D, 0x14, // turn power on
    };

    static uint8_t turnOnCommand = 0xAF;

    lcd_reset(i2c);
    HAL_I2C_Mem_Write(i2c, LCD_I2C_WRITE_ADDRESS, LCD_I2C_COMMAND_CONTROL_BYTE, 1, initCommands, sizeof(initCommands), sizeof(initCommands) * 10);

    lcd_reset_screen();
    HAL_I2C_Mem_Write(i2c, LCD_I2C_WRITE_ADDRESS, LCD_I2C_DATA_CONTROL_BYTE, 1, buf_front, LCD_BUF_SIZE, LCD_BUF_SIZE * 10);

    HAL_I2C_Mem_Write(i2c, LCD_I2C_WRITE_ADDRESS, LCD_I2C_COMMAND_CONTROL_BYTE, 1, &turnOnCommand, 1, 10);
}

void lcd_step(I2C_HandleTypeDef * i2c) {
    static uint8_t * to_send = NULL;
    static size_t remaining = 0;

    if (remaining > 0) {
        const size_t will_send = min(remaining, LCD_MAX_BYTES_PER_STEP);
        HAL_I2C_Mem_Write_IT(i2c, LCD_I2C_WRITE_ADDRESS, LCD_I2C_DATA_CONTROL_BYTE, 1, to_send, will_send);

        to_send += will_send;
        remaining -= will_send;
    } else {
        to_send = buf_front;
        remaining = LCD_BUF_SIZE;

        static uint8_t resetPositionCommands[] = {
                0x21, 0x00, 0x7F, // set columns range 0..127
                0x22, 0x00, 0x07, // set rows range 0..7
        };

        HAL_I2C_Mem_Write_IT(i2c, LCD_I2C_WRITE_ADDRESS, LCD_I2C_COMMAND_CONTROL_BYTE, 1, resetPositionCommands, sizeof(resetPositionCommands));
    }
}

void lcd_done() {
    const uint32_t priMask = __get_PRIMASK();
    __disable_irq();

    memcpy(buf_front, buf_back, LCD_BUF_SIZE);

    __set_PRIMASK(priMask);
}

void lcd_reset_screen() {
    memset(buf_back, 0, LCD_BUF_SIZE);
}

void lcd_set_pixel(uint8_t x, uint8_t y, bool color) {
    if (x >= LCD_WIDTH || y >= LCD_HEIGHT) {
        return;
    }

    const uint16_t idx = y / 8 * LCD_WIDTH + x;
    const uint8_t mask = 1 << (y % 8);

    if (color) {
        buf_back[idx] = buf_back[idx] | mask;
    } else {
        buf_back[idx] = buf_back[idx] & ~mask;
    }
}

void lcd_fill_rect(uint8_t ax, uint8_t ay, uint8_t bx, uint8_t by, bool color) {
    for (uint8_t y = ay; y <= by; ++y) {
        for (uint8_t x = ax; x <= bx; ++x) {
            lcd_set_pixel(x, y, color);
        }
    }
}

void lcd_draw_sprite(uint8_t start_x, uint8_t start_y, const struct lcd_sprite * sprite, bool color, bool transparent) {
    const uint8_t * ptr = sprite->content;
    uint8_t current_byte = *ptr;
    uint8_t cb_offset = 0;

    const uint8_t end_x = start_x + sprite->width;
    const uint8_t end_y = start_y + sprite->height;
    for (uint8_t y = start_y; y < end_y; ++y) {
        for (uint8_t x = start_x; x < end_x; ++x) {
            if (current_byte & 0x80) {
                lcd_set_pixel(x, y, color);
            } else if (!transparent) {
                lcd_set_pixel(x, y, !color);
            }

            current_byte <<= 1;
            ++cb_offset;

            if (cb_offset == 8) {
                cb_offset = 0;

                ++ptr;
                current_byte = *ptr;
            }
        }
    }
}

void lcd_draw_char(uint8_t x, uint8_t y, const struct lcd_font * font, char c, bool color, bool transparent) {
    if (x >= LCD_WIDTH || y >= LCD_HEIGHT) {
        return;
    }

    const size_t sprite_offset = (c - 32) * ceil(font->width * font->height, 8);
    const struct lcd_sprite sprite = {
        .width = font->width,
        .height = font->height,
        .content = font->content + sprite_offset,
    };

    lcd_draw_sprite(x, y, &sprite, color, transparent);
}

void lcd_draw_string(uint8_t x, uint8_t y, const struct lcd_font * font, const char * s, bool color, bool transparent) {
    uint8_t current_x = x;

    for (; *s; ++s) {
        if (current_x + font->width >= LCD_WIDTH) {
            current_x = x;
            y += font->height;
        }

        lcd_draw_char(current_x, y, font, *s, color, transparent);
        current_x += font->width;
    }
}

