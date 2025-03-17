package com.alejandrapazrivas.juego10000.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val playerId: Long = 0,
    val name: String,
    val avatarResourceId: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val highestScore: Int = 0,
    val totalScore: Long = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)