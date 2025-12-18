package com.bigotitech.rokub10000.presentation.common.util

import com.bigotitech.rokub10000.R

/**
 * Utilidades para la representación visual de dados.
 */

/**
 * Obtiene el drawable correspondiente al valor de un dado.
 *
 * @param value Valor del dado (1-6)
 * @return ID del recurso drawable correspondiente
 */
fun getDiceDrawable(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1
    }
}
