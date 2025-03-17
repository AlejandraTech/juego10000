package com.alejandrapazrivas.juego10000.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alejandrapazrivas.juego10000.util.DateConverter
import java.util.Date

@Entity(
    tableName = "scores",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["playerId"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["gameId"]),
        Index(value = ["playerId"])
    ]
)
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val scoreId: Long = 0,
    val gameId: Long,
    val playerId: Long,
    val round: Int,
    val turnScore: Int,
    val totalScore: Int,
    val diceRolls: Int = 0,

    @TypeConverters(DateConverter::class)
    val timestamp: Date = Date()
)