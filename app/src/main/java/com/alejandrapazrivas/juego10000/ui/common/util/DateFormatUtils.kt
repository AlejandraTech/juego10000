package com.alejandrapazrivas.juego10000.ui.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utilidades para formatear fechas en la aplicación
 */
object DateFormatUtils {

    private val fullDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    /**
     * Formatea una fecha con formato completo (día/mes/año hora:minuto)
     */
    fun formatFullDate(date: Date): String {
        return fullDateFormat.format(date)
    }

    /**
     * Formatea una fecha con formato corto (día/mes/año)
     */
    fun formatShortDate(date: Date): String {
        return shortDateFormat.format(date)
    }
}
