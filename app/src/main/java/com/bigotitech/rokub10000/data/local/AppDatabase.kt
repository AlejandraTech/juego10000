package com.bigotitech.rokub10000.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bigotitech.rokub10000.data.local.dao.GameDao
import com.bigotitech.rokub10000.data.local.dao.PlayerDao
import com.bigotitech.rokub10000.data.local.dao.ScoreDao
import com.bigotitech.rokub10000.data.local.entity.GameEntity
import com.bigotitech.rokub10000.data.local.entity.PlayerEntity
import com.bigotitech.rokub10000.data.local.entity.ScoreEntity
import com.bigotitech.rokub10000.util.DateConverter
import com.bigotitech.rokub10000.util.ListConverter

/**
 * Clase principal de la base de datos Room para la aplicación.
 *
 * Define las entidades, versión y convertidores de tipos utilizados en la base de datos.
 * Proporciona acceso a los DAOs para interactuar con las diferentes tablas.
 */
@Database(
    entities = [
        PlayerEntity::class,
        GameEntity::class,
        ScoreEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de jugadores.
     */
    abstract fun playerDao(): PlayerDao

    /**
     * Proporciona acceso al DAO de juegos.
     */
    abstract fun gameDao(): GameDao

    /**
     * Proporciona acceso al DAO de puntuaciones.
     */
    abstract fun scoreDao(): ScoreDao

    companion object {
        private const val DATABASE_NAME = "rokub10000.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene una instancia de la base de datos, creándola si no existe.
         *
         * Utiliza el patrón Singleton para garantizar una única instancia de la base de datos.
         * Las migraciones se aplican automáticamente para preservar los datos del usuario.
         *
         * @param context Contexto de la aplicación
         * @return Instancia de AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
