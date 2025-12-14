package com.bigotitech.rokub10000.util

import androidx.room.TypeConverter
import java.util.Date

/**
 * Conversor de tipos para fechas en Room Database.
 * 
 * Esta clase proporciona métodos para convertir entre objetos Date y valores Long (timestamp),
 * permitiendo almacenar fechas en la base de datos Room.
 */
class DateConverter {
    /**
     * Convierte un timestamp (Long) a un objeto Date.
     * 
     * @param value Timestamp en milisegundos desde epoch
     * @return Objeto Date correspondiente al timestamp, o null si la entrada es null
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Convierte un objeto Date a un timestamp (Long).
     * 
     * @param date Objeto Date a convertir
     * @return Timestamp en milisegundos desde epoch, o null si la entrada es null
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}