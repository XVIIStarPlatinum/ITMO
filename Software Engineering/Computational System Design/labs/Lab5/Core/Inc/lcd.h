#pragma once

#include <stdbool.h>

#include "main.h"


#define LCD_WIDTH 128
#define LCD_HEIGHT 64


// content is sequence of bits, where each bit is one pixel
// and new line started from end of previous
struct lcd_sprite {
	uint8_t width;
	uint8_t height;
	const uint8_t * content;
};

struct lcd_font {
    uint8_t width;
    uint8_t height;
    const uint8_t * content;
};


void lcd_init(I2C_HandleTypeDef * i2c);
void lcd_step(I2C_HandleTypeDef * i2c);

void lcd_done();
void lcd_reset_screen();
void lcd_set_pixel(uint8_t x, uint8_t y, bool color);
void lcd_fill_rect(uint8_t ax, uint8_t ay, uint8_t bx, uint8_t by, bool color);
void lcd_draw_sprite(uint8_t x, uint8_t y, const struct lcd_sprite * sprite, bool color, bool transparent);
void lcd_draw_char(uint8_t x, uint8_t y, const struct lcd_font * font, char c, bool color, bool transparent);
void lcd_draw_string(uint8_t x, uint8_t y, const struct lcd_font * font, const char * s, bool color, bool transparent);
