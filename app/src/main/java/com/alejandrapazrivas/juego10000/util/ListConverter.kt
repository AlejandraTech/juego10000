package com.alejandrapazrivas.juego10000.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Conversor de tipos para listas en Room Database.
 * 
 * Esta clase proporciona métodos genéricos para convertir listas de cualquier tipo a cadenas JSON
 * y viceversa, permitiendo almacenar colecciones en la base de datos Room.
 */
class ListConverter {
    private val gson = Gson()

    /**
     * Convierte una lista de Long a una cadena JSON.
     * 
     * @param value Lista de Long a convertir
     * @return Representación JSON de la lista como String, o null si la entrada es null
     */
    @TypeConverter
    fun fromLongList(value: List<Long>?): String? {
        return fromList(value)
    }

    /**
     * Convierte una cadena JSON a una lista de Long.
     * 
     * @param value Cadena JSON a convertir
     * @return Lista de Long, o una lista vacía si la entrada es null
     */
    @TypeConverter
    fun toLongList(value: String?): List<Long> {
        return toList(value, object : TypeToken<List<Long>>() {}.type)
    }

    /**
     * Método genérico para convertir cualquier lista a una cadena JSON.
     * 
     * @param list Lista a convertir
     * @return Representación JSON de la lista como String, o null si la entrada es null
     */
    private fun <T> fromList(list: List<T>?): String? {
        return gson.toJson(list)
    }

    /**
     * Método genérico para convertir una cadena JSON a una lista del tipo especificado.
     * 
     * @param value Cadena JSON a convertir
     * @param type Tipo de la lista a devolver
     * @return Lista del tipo especificado, o una lista vacía si la entrada es null
     */
    private fun <T> toList(value: String?, type: Type): List<T> {
        if (value == null) return emptyList()
        return gson.fromJson(value, type)
    }
}