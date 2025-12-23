package com.bigotitech.rokub10000.domain.usecase.game

import android.util.Log
import com.bigotitech.rokub10000.domain.repository.GameRepository
import com.bigotitech.rokub10000.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva partida.
 */
class CreateGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository
) {
    companion object {
        const val MODE_MULTIPLAYER = "MULTIPLAYER"
        const val MODE_SINGLE_PLAYER = "SINGLE_PLAYER"
        const val DEFAULT_TARGET_SCORE = 10000
    }

    /**
     * Crea una nueva partida con los jugadores seleccionados
     * @param playerIds IDs de los jugadores participantes
     * @param targetScore Puntuación objetivo para ganar (por defecto 10000)
     * @param gameMode Modo de juego (MULTIPLAYER, SINGLE_PLAYER)
     * @param includeBotPlayer Si es true, añade un jugador Bot a la partida (para modo individual)
     * @param botName Nombre del Bot (por defecto "Bot")
     * @param botDifficulty Dificultad del bot (BEGINNER, INTERMEDIATE, EXPERT)
     * @return ID de la partida creada
     */
    suspend operator fun invoke(
        playerIds: List<Long>,
        targetScore: Int = DEFAULT_TARGET_SCORE,
        gameMode: String = MODE_MULTIPLAYER,
        includeBotPlayer: Boolean = false,
        botName: String = "Bot",
        botDifficulty: String = "INTERMEDIATE"
    ): Result<Long> {
        return try {
            if (playerIds.isEmpty()) {
                return Result.failure(IllegalArgumentException("Se requiere al menos un jugador"))
            }

            val finalPlayerIds = prepareFinalPlayerList(playerIds, gameMode, includeBotPlayer, botName, botDifficulty)

            val gameId = gameRepository.createGame(
                playerIds = finalPlayerIds,
                targetScore = targetScore,
                gameMode = gameMode,
                additionalData = if (includeBotPlayer) {
                    mapOf(
                        "botName" to botName,
                        "botDifficulty" to botDifficulty
                    )
                } else emptyMap()
            )

            Result.success(gameId)
        } catch (e: Exception) {
            Log.e("CreateGameUseCase", "Error al crear juego: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Prepara la lista final de jugadores, reutilizando un bot existente si es necesario
     */
    private suspend fun prepareFinalPlayerList(
        playerIds: List<Long>,
        gameMode: String,
        includeBotPlayer: Boolean,
        botName: String,
        botDifficulty: String
    ): List<Long> {
        if (includeBotPlayer && gameMode == MODE_SINGLE_PLAYER) {
            val botPlayerId = playerRepository.getOrCreateBot(
                difficulty = botDifficulty,
                botName = botName
            )
            return playerIds + botPlayerId
        }

        return playerIds
    }
}
