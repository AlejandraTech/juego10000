package com.alejandrapazrivas.juego10000.domain.usecase

import android.util.Log
import com.alejandrapazrivas.juego10000.domain.repository.GameRepository
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import com.google.gson.Gson
import javax.inject.Inject

/**
 * Caso de uso para guardar el estado de la partida y gestionar su finalización.
 */
class SaveGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val gson: Gson
) {
    /**
     * Guarda el estado actual de la partida
     * @param gameId ID de la partida
     * @param currentPlayerIndex Índice del jugador actual
     * @param currentRound Ronda actual
     * @param gameStateData Datos adicionales del estado del juego
     */
    suspend operator fun invoke(
        gameId: Long,
        currentPlayerIndex: Int,
        currentRound: Int,
        gameStateData: Map<String, Any>
    ): Result<Unit> {
        return try {
            val gameState = gson.toJson(gameStateData)

            gameRepository.updateGameState(
                gameId = gameId,
                currentPlayerIndex = currentPlayerIndex,
                currentRound = currentRound,
                gameState = gameState
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Completa una partida y registra el ganador
     * @param gameId ID de la partida
     * @param winnerPlayerId ID del jugador ganador
     */
    suspend fun completeGame(gameId: Long, winnerPlayerId: Long): Result<Unit> {
        return try {
            gameRepository.completeGame(gameId, winnerPlayerId)

            cleanupGameResources(gameId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SaveGameUseCase", "Error al completar juego: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Limpia los recursos asociados a una partida (como eliminar bots en modo individual)
     */
    private suspend fun cleanupGameResources(gameId: Long) {
        try {
            val game = gameRepository.getGameById(gameId)

            if (game != null && game.gameMode == "SINGLE_PLAYER") {
                val botPlayerId = game.playerIds.find { playerId -> 
                    val player = playerRepository.getPlayerById(playerId)
                    player?.name == "Bot"
                }

                if (botPlayerId != null) {
                    Log.d("SaveGameUseCase", "Eliminando Bot con ID: $botPlayerId")
                    playerRepository.deletePlayer(botPlayerId)
                }
            }
        } catch (e: Exception) {
            Log.e("SaveGameUseCase", "Error al limpiar recursos: ${e.message}", e)
        }
    }
    
    /**
     * Elimina un jugador bot de la base de datos
     * @param botPlayerId ID del jugador bot a eliminar
     */
    suspend fun deleteBotPlayer(botPlayerId: Long): Result<Unit> {
        return try {
            Log.d("SaveGameUseCase", "Eliminando jugador bot con ID: $botPlayerId")

            val player = playerRepository.getPlayerById(botPlayerId)
            if (player != null && player.name == "Bot") {
                playerRepository.deletePlayer(botPlayerId)
                Log.d("SaveGameUseCase", "Bot eliminado correctamente")
            } else {
                Log.w("SaveGameUseCase", "No se eliminó el jugador porque no es un bot o no existe")
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SaveGameUseCase", "Error al eliminar bot: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Guarda la puntuación de un turno
     * @param gameId ID de la partida
     * @param playerId ID del jugador
     * @param round Ronda actual
     * @param turnScore Puntuación del turno
     * @param totalScore Puntuación total acumulada
     * @param diceRolls Número de lanzamientos en el turno
     */
    suspend fun saveScore(
        gameId: Long,
        playerId: Long,
        round: Int,
        turnScore: Int,
        totalScore: Int,
        diceRolls: Int
    ): Result<Long> {
        return try {
            val scoreId = gameRepository.saveScore(
                gameId = gameId,
                playerId = playerId,
                round = round,
                turnScore = turnScore,
                totalScore = totalScore,
                diceRolls = diceRolls
            )
            Result.success(scoreId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
