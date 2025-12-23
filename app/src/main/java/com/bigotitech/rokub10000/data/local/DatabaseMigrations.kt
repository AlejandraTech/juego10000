package com.bigotitech.rokub10000.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones de la base de datos Room.
 * Cada migración define cómo actualizar el esquema de una versión a otra
 * sin perder los datos existentes.
 */
object DatabaseMigrations {

    /**
     * Migración de versión 1 a 2.
     * Añade el campo isBot a la tabla players para identificar jugadores bot.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE players ADD COLUMN isBot INTEGER NOT NULL DEFAULT 0")
        }
    }

    /**
     * Migración de versión 2 a 3.
     * Añade el campo botDifficulty a la tabla players para identificar la dificultad del bot.
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE players ADD COLUMN botDifficulty TEXT DEFAULT NULL")
        }
    }

    /**
     * Lista de todas las migraciones disponibles.
     * Añadir nuevas migraciones aquí cuando se actualice el esquema.
     */
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3
    )
}
