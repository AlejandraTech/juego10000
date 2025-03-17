package com.alejandrapazrivas.juego10000.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alejandrapazrivas.juego10000.data.local.dao.GameDao
import com.alejandrapazrivas.juego10000.data.local.dao.PlayerDao
import com.alejandrapazrivas.juego10000.data.local.dao.ScoreDao
import com.alejandrapazrivas.juego10000.data.local.entity.GameEntity
import com.alejandrapazrivas.juego10000.data.local.entity.PlayerEntity
import com.alejandrapazrivas.juego10000.data.local.entity.ScoreEntity
import com.alejandrapazrivas.juego10000.util.DateConverter
import com.alejandrapazrivas.juego10000.util.ListConverter

@Database(
    entities = [
        PlayerEntity::class,
        GameEntity::class,
        ScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun scoreDao(): ScoreDao

    companion object {
        private const val DATABASE_NAME = "juego10000.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}