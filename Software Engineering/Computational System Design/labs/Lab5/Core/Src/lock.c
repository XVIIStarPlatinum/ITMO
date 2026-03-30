/* lock.c — модифицированная версия:
   - игнор дублирующих нажатий (коротких повторов) внутри lock (simple debounce)
   - флаги предотвращают перерисовку idle/ change поверх области ввода
   - render_input_stars использует font->width и держит ввод на экране
*/

#include "lock.h"
#include "lcd.h"
#include "sprites.h"

#include <string.h>

#define PASSWORD_MIN 8
#define PASSWORD_MAX 12

#define LOCK_REPEAT_GUARD_MS 200

typedef enum {
    LOCK_IDLE,
    LOCK_ENTER,
    LOCK_CHANGE_WAIT,
    LOCK_CHANGE_ENTER,
    LOCK_SHOW_MESSAGE
} lock_state_t;

static I2C_HandleTypeDef *lcd_i2c;

static lock_state_t state = LOCK_IDLE;

static char stored_password[PASSWORD_MAX + 1] = "12345678";
static char input_buffer[PASSWORD_MAX + 1];
static uint8_t input_len = 0;
static uint32_t msg_timer = 0;
static bool idle_shown = false;
static bool change_shown = false;
static bool input_shown = false;
static char last_key = 0;
static uint32_t last_key_tick = 0;

static void lcd_center(const char *msg)
{
    idle_shown = false;
    change_shown = false;
    input_shown = false;

    lcd_reset_screen();
    lcd_draw_string(0, 0, &font_7x10, msg, 1, false);
    lcd_done();
}

static void screen_idle()
{
    if (input_shown) return;

    if (idle_shown) return;
    idle_shown = true;
    change_shown = false;

    lcd_reset_screen();
    lcd_draw_string(0, 0, &font_7x10, "Enter password:", 1, false);
    lcd_done();
}

static void screen_change()
{
    if (input_shown) return;
    if (change_shown) return;
    change_shown = true;
    idle_shown = false;

    lcd_reset_screen();
    lcd_draw_string(0, 0, &font_7x10, "New password:", 1, false);
    lcd_done();
}

static void show_msg(const char *msg, uint32_t duration)
{
    lcd_center(msg);
    msg_timer = duration;
    state = LOCK_SHOW_MESSAGE;
    input_shown = false;
}

static void finish_enter()
{
    if (strcmp(input_buffer, stored_password) == 0) {
        show_msg("OK", 1000);
    } else {
        show_msg("Wrong", 1500);
    }

    input_len = 0;
    input_buffer[0] = '\0';
    input_shown = false;
}

static void finish_change()
{
    if (input_len < PASSWORD_MIN || input_len > PASSWORD_MAX) {
        show_msg("Bad length", 1500);
    } else {
        strncpy(stored_password, input_buffer, PASSWORD_MAX);
        stored_password[input_len] = '\0';
        show_msg("Saved", 1000);
    }

    input_len = 0;
    input_buffer[0] = '\0';
    input_shown = false;
}

static void render_input_stars(void)
{
    lcd_fill_rect(0, 20, 127, 63, 0);

    const struct lcd_font *f = &font_7x10;
    const uint8_t char_w = (f && f->width) ? f->width : 8;

    uint8_t rect_w = char_w > 3 ? (char_w - 2) : 4;
    if (rect_w > 8) rect_w = 8;
    const uint8_t rect_h = 8;

    for (uint8_t i = 0; i < input_len; ++i) {
        uint8_t x = i * char_w + (char_w > rect_w ? (char_w - rect_w) / 2 : 0);
        uint8_t y = 22;
        lcd_fill_rect(x, y, x + rect_w - 1, y + rect_h - 1, true);
    }

    lcd_done();
    input_shown = true;
}

void lock_key_pressed(char key)
{
    uint32_t now = HAL_GetTick();
    if (key == last_key && (uint32_t)(now - last_key_tick) < LOCK_REPEAT_GUARD_MS) {
        return;
    }
    last_key = key;
    last_key_tick = now;

    if (state == LOCK_SHOW_MESSAGE) return;

    if (key == '#') {
        state = LOCK_CHANGE_WAIT;
        lcd_center("Change?");
        return;
    }

    if (key == '*') {
        if (state == LOCK_ENTER) {
            finish_enter();
            state = LOCK_IDLE;
            return;
        } else if (state == LOCK_CHANGE_WAIT) {
            state = LOCK_CHANGE_ENTER;
            input_len = 0;
            input_buffer[0] = '\0';
            screen_change();
            return;
        } else if (state == LOCK_CHANGE_ENTER) {
            finish_change();
            state = LOCK_IDLE;
            return;
        }
        return;
    }

    if (key >= '0' && key <= '9') {
        if (state == LOCK_IDLE) {
            state = LOCK_ENTER;
            input_len = 0;
            input_buffer[0] = '\0';
            lcd_reset_screen();
            lcd_draw_string(0, 0, &font_7x10, "Enter:", 1, false);
            lcd_done();
            idle_shown = false;
        }

        if (state == LOCK_ENTER || state == LOCK_CHANGE_ENTER) {
            if (input_len < PASSWORD_MAX) {
                input_buffer[input_len++] = key;
                input_buffer[input_len] = '\0';
                render_input_stars();
            }
        }
    }
}

void lock_init(I2C_HandleTypeDef *hi2c)
{
    lcd_i2c = hi2c;
    screen_idle();
}

void lock_step(I2C_HandleTypeDef *hi2c)
{
    (void)hi2c;
    lcd_step(lcd_i2c);

    if (state == LOCK_SHOW_MESSAGE) {
        if (msg_timer > 0) {
            msg_timer--;
        } else {
            input_shown = false;
            screen_idle();
            state = LOCK_IDLE;
        }
    }
}
