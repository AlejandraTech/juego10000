package com.alejandrapazrivas.juego10000.domain.usecase

import android.util.Log
import com.alejandrapazrivas.juego10000.domain.repository.GameRepository
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
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
     * @return ID de la partida creada
     */
    suspend operator fun invoke(
        playerIds: List<Long>,
        targetScore: Int = DEFAULT_TARGET_SCORE,
        gameMode: String = MODE_MULTIPLAYER,
        includeBotPlayer: Boolean = false,
        botName: String = "Bot"
    ): Result<Long> {
        return try {
            // Validar parámetros
            if (playerIds.isEmpty()) {
                return Result.failure(IllegalArgumentException("Se requiere al menos un jugador"))
            }

            // Preparar lista final de jugadores (añadir bot si es necesario)
            val finalPlayerIds = prepareFinalPlayerList(playerIds, gameMode, includeBotPlayer, botName)

            // Crear la partida
            val gameId = gameRepository.createGame(
                playerIds = finalPlayerIds,
                targetScore = targetScore,
                gameMode = gameMode,
                additionalData = if (includeBotPlayer) {
                    mapOf(
                        "botName" to botName,
                        "isBot" to true
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
     * Prepara la lista final de jugadores, añadiendo un bot si es necesario
     */
    private suspend fun prepareFinalPlayerList(
        playerIds: List<Long>,
        gameMode: String,
        includeBotPlayer: Boolean,
        botName: String
    ): List<Long> {
        // Si es modo individual y se solicita incluir un Bot, crear un jugador real para el Bot en la BD
        if (includeBotPlayer && gameMode == MODE_SINGLE_PLAYER) {
            Log.d("CreateGameUseCase", "Creando juego con Bot")
            
            // Crear un jugador real para el Bot en la base de datos
            val botPlayerId = playerRepository.createPlayer(
                name = botName,
                avatarResourceId = 0
            )
            
            Log.d("CreateGameUseCase", "Bot creado con ID: $botPlayerId")
            
            // Añadir el ID del Bot a la lista de jugadores
            return playerIds + botPlayerId
        }
        
        return playerIds
    }
}
