package com.alejandrapazrivas.juego10000.domain.repository

import com.alejandrapazrivas.juego10000.domain.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar las operaciones relacionadas con partidas.
 */
interface GameRepository {
    /**
     * Crea una nueva partida.
     * 
     * @param playerIds Lista de IDs de los jugadores participantes
     * @param targetScore Puntuación objetivo para ganar la partida
     * @param gameMode Modo de juego (MULTIPLAYER, SINGLE_PLAYER, etc.)
     * @param additionalData Datos adicionales para la partida
     * @return ID de la partida creada
     */
    suspend fun createGame(
        playerIds: List<Long>,
        targetScore: Int = 10000,
        gameMode: String = "MULTIPLAYER",
        additionalData: Map<String, Any> = emptyMap()
    ): Long

    /**
     * Obtiene una partida por su ID.
     * 
     * @param gameId ID de la partida
     * @return La partida si existe, o null si no se encuentra
     */
    suspend fun getGameById(gameId: Long): Game?
    
    /**
     * Obtiene una partida por su ID como un flujo observable.
     * 
     * @param gameId ID de la partida
     * @return Flujo que emite la partida si existe, o null si no se encuentra
     */
    fun getGameByIdFlow(gameId: Long): Flow<Game?>
    
    /**
     * Obtiene todas las partidas activas (no completadas).
     * 
     * @return Flujo que emite la lista de partidas activas
     */
    fun getActiveGames(): Flow<List<Game>>
    
    /**
     * Obtiene las partidas completadas más recientes.
     * 
     * @return Flujo que emite la lista de partidas completadas recientemente
     */
    fun getRecentCompletedGames(): Flow<List<Game>>
    
    /**
     * Obtiene todas las partidas en las que ha participado un jugador.
     * 
     * @param playerId ID del jugador
     * @return Flujo que emite la lista de partidas del jugador
     */
    fun getPlayerGames(playerId: Long): Flow<List<Game>>
    
    /**
     * Actualiza el estado de una partida.
     * 
     * @param gameId ID de la partida
     * @param currentPlayerIndex Índice del jugador actual
     * @param currentRound Número de ronda actual
     * @param gameState Estado serializado del juego
     */
    suspend fun updateGameState(
        gameId: Long,
        currentPlayerIndex: Int,
        currentRound: Int,
        gameState: String?
    )

    /**
     * Completa una partida y actualiza las estadísticas de los jugadores.
     * 
     * @param gameId ID de la partida
     * @param winnerPlayerId ID del jugador ganador
     */
    suspend fun completeGame(gameId: Long, winnerPlayerId: Long)
    
    /**
     * Guarda la puntuación de un turno y actualiza las estadísticas del jugador.
     * 
     * @param gameId ID de la partida
     * @param playerId ID del jugador
     * @param round Número de ronda
     * @param turnScore Puntuación del turno
     * @param totalScore Puntuación total acumulada
     * @param diceRolls Número de lanzamientos de dados en el turno
     * @return ID de la puntuación guardada
     */
    suspend fun saveScore(
        gameId: Long,
        playerId: Long,
        round: Int,
        turnScore: Int,
        totalScore: Int,
        diceRolls: Int
    ): Long

    /**
     * Elimina una partida de la base de datos.
     * 
     * @param gameId ID de la partida a eliminar
     */
    suspend fun deleteGame(gameId: Long)
}
