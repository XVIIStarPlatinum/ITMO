#ifndef LOCK_H
#define LOCK_H

#include "main.h"
#include "kb.h"
#include <stdbool.h>
#include <stdint.h>

void lock_init(I2C_HandleTypeDef *hi2c);
void lock_step(I2C_HandleTypeDef *hi2c);

// Called when a key character is ready (e.g. '0'..'9', '*' or '#')
void lock_key_pressed(char key);

#endif // LOCK_H
