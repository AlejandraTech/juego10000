package com.bigotitech.rokub10000.data.repository

import com.bigotitech.rokub10000.data.local.entity.GameEntity
import com.bigotitech.rokub10000.data.local.entity.PlayerEntity
import com.bigotitech.rokub10000.data.local.entity.ScoreEntity
import com.bigotitech.rokub10000.domain.model.Game
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.model.Score

/**
 * Clase base para los repositorios que proporciona funciones de mapeo comunes.
 * Centraliza la lógica de conversión entre entidades y modelos de dominio.
 */
abstract class BaseRepository {

    /**
     * Convierte una entidad GameEntity a un modelo de dominio Game.
     */
    protected fun GameEntity.toDomainModel(): Game = Game(
        id = this.gameId,
        playerIds = this.playerIds,
        winnerPlayerId = this.winnerPlayerId,
        targetScore = this.targetScore,
        isCompleted = this.isCompleted,
        currentPlayerIndex = this.currentPlayerIndex,
        currentRound = this.currentRound,
        startedAt = this.startedAt,
        completedAt = this.completedAt,
        gameMode = this.gameMode,
        gameState = this.gameState
    )

    /**
     * Convierte una entidad PlayerEntity a un modelo de dominio Player.
     */
    protected fun PlayerEntity.toDomainModel(): Player = Player(
        id = this.playerId,
        name = this.name,
        avatarResourceId = this.avatarResourceId,
        gamesPlayed = this.gamesPlayed,
        gamesWon = this.gamesWon,
        highestScore = this.highestScore,
        totalScore = this.totalScore,
        isActive = this.isActive,
        isBot = this.isBot,
        botDifficulty = this.botDifficulty,
        createdAt = this.createdAt
    )

    /**
     * Convierte una entidad ScoreEntity a un modelo de dominio Score.
     */
    protected fun ScoreEntity.toDomainModel(): Score = Score(
        id = this.scoreId,
        gameId = this.gameId,
        playerId = this.playerId,
        round = this.round,
        turnScore = this.turnScore,
        totalScore = this.totalScore,
        diceRolls = this.diceRolls,
        timestamp = this.timestamp
    )

    /**
     * Convierte un modelo de dominio Player a una entidad PlayerEntity.
     */
    protected fun Player.toEntity(): PlayerEntity = PlayerEntity(
        playerId = this.id,
        name = this.name,
        avatarResourceId = this.avatarResourceId,
        gamesPlayed = this.gamesPlayed,
        gamesWon = this.gamesWon,
        highestScore = this.highestScore,
        totalScore = this.totalScore,
        isActive = this.isActive,
        isBot = this.isBot,
        botDifficulty = this.botDifficulty,
        createdAt = this.createdAt
    )
}
