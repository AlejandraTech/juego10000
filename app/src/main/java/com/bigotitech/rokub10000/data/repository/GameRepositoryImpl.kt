package com.bigotitech.rokub10000.data.repository

import com.bigotitech.rokub10000.data.local.dao.GameDao
import com.bigotitech.rokub10000.data.local.dao.PlayerDao
import com.bigotitech.rokub10000.data.local.dao.ScoreDao
import com.bigotitech.rokub10000.data.local.entity.GameEntity
import com.bigotitech.rokub10000.data.local.entity.ScoreEntity
import com.bigotitech.rokub10000.domain.model.Game
import com.bigotitech.rokub10000.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de juegos.
 * Gestiona la creación, actualización y consulta de partidas.
 */
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val playerDao: PlayerDao,
    private val scoreDao: ScoreDao
) : BaseRepository(), GameRepository {

    /**
     * Crea una nueva partida con los jugadores, puntuación objetivo y modo de juego especificados.
     *
     * @param playerIds Lista de IDs de los jugadores participantes
     * @param targetScore Puntuación objetivo para ganar la partida
     * @param gameMode Modo de juego (MULTIPLAYER, SINGLE_PLAYER, etc.)
     * @param additionalData Datos adicionales para la partida
     * @return ID de la partida creada
     */
    override suspend fun createGame(
        playerIds: List<Long>,
        targetScore: Int,
        gameMode: String,
        additionalData: Map<String, Any>
    ): Long {
        val gameStateJson = prepareGameState(gameMode, additionalData, playerIds)

        val gameEntity = GameEntity(
            playerIds = playerIds,
            targetScore = targetScore,
            gameMode = gameMode,
            gameState = gameStateJson
        )

        return gameDao.insertGame(gameEntity)
    }

    /**
     * Prepara el estado inicial del juego basado en el modo y datos adicionales.
     *
     * @param gameMode Modo de juego
     * @param additionalData Datos adicionales para la partida
     * @param playerIds Lista de IDs de los jugadores participantes
     * @return Estado del juego serializado como cadena, o null si no hay datos adicionales
     */
    private fun prepareGameState(
        gameMode: String,
        additionalData: Map<String, Any>,
        playerIds: List<Long>
    ): String? {
        if (additionalData.isEmpty()) {
            return null
        }

        val initialState = mutableMapOf<String, Any>()

        if (gameMode == "SINGLE_PLAYER" && additionalData.containsKey("botName")) {
            val botName = additionalData["botName"] as? String ?: "Bot"
            initialState["botName"] = botName

            val botPlayerId = playerIds.find { it < 0 }
            if (botPlayerId != null) {
                initialState["botPlayerId"] = botPlayerId
            }
        }

        return initialState.entries.joinToString(";") { "${it.key}=${it.value}" }
    }

    /**
     * Obtiene una partida por su ID.
     *
     * @param gameId ID de la partida a obtener
     * @return La partida si existe, o null si no se encuentra
     */
    override suspend fun getGameById(gameId: Long): Game? {
        val gameEntity = gameDao.getGameById(gameId) ?: return null
        return gameEntity.toDomainModel()
    }

    /**
     * Obtiene una partida por su ID como un flujo observable.
     *
     * @param gameId ID de la partida a obtener
     * @return Flujo que emite la partida si existe, o null si no se encuentra
     */
    override fun getGameByIdFlow(gameId: Long): Flow<Game?> {
        return gameDao.getGameByIdFlow(gameId).map { it?.toDomainModel() }
    }

    /**
     * Obtiene todas las partidas activas (no completadas).
     *
     * @return Flujo que emite la lista de partidas activas
     */
    override fun getActiveGames(): Flow<List<Game>> {
        return gameDao.getActiveGames().map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene las partidas completadas más recientes.
     *
     * @return Flujo que emite la lista de partidas completadas recientemente
     */
    override fun getRecentCompletedGames(): Flow<List<Game>> {
        return gameDao.getRecentCompletedGames().map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene todas las partidas en las que ha participado un jugador.
     *
     * @param playerId ID del jugador
     * @return Flujo que emite la lista de partidas del jugador
     */
    override fun getPlayerGames(playerId: Long): Flow<List<Game>> {
        return gameDao.getPlayerGames(playerId).map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Actualiza el estado de una partida.
     *
     * @param gameId ID de la partida a actualizar
     * @param currentPlayerIndex Índice del jugador actual
     * @param currentRound Número de ronda actual
     * @param gameState Estado serializado del juego
     */
    override suspend fun updateGameState(
        gameId: Long,
        currentPlayerIndex: Int,
        currentRound: Int,
        gameState: String?
    ) {
        gameDao.updateGameState(gameId, currentPlayerIndex, currentRound, gameState)
    }

    /**
     * Completa una partida y actualiza las estadísticas de los jugadores.
     *
     * @param gameId ID de la partida a completar
     * @param winnerPlayerId ID del jugador ganador
     */
    override suspend fun completeGame(gameId: Long, winnerPlayerId: Long) {
        val game = getGameById(gameId) ?: return

        gameDao.completeGame(gameId, winnerPlayerId)

        updatePlayerStats(winnerPlayerId, game.playerIds)
    }

    /**
     * Actualiza las estadísticas de los jugadores después de completar una partida.
     *
     * @param winnerPlayerId ID del jugador ganador
     * @param allPlayerIds Lista de IDs de todos los jugadores en la partida
     */
    private suspend fun updatePlayerStats(winnerPlayerId: Long, allPlayerIds: List<Long>) {
        playerDao.incrementGamesWon(winnerPlayerId)

        val realPlayerIds = allPlayerIds.filter { it > 0 }
        if (realPlayerIds.isNotEmpty()) {
            playerDao.incrementGamesPlayed(realPlayerIds)
        }
    }

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
    override suspend fun saveScore(
        gameId: Long,
        playerId: Long,
        round: Int,
        turnScore: Int,
        totalScore: Int,
        diceRolls: Int
    ): Long {
        val scoreEntity = ScoreEntity(
            gameId = gameId,
            playerId = playerId,
            round = round,
            turnScore = turnScore,
            totalScore = totalScore,
            diceRolls = diceRolls
        )

        val scoreId = scoreDao.insertScore(scoreEntity)

        playerDao.updateHighestScore(playerId, totalScore)
        playerDao.updateTotalScore(playerId, turnScore)

        return scoreId
    }

    /**
     * Elimina una partida de la base de datos.
     *
     * @param gameId ID de la partida a eliminar
     */
    override suspend fun deleteGame(gameId: Long) {
        gameDao.deleteGame(gameId)
    }
}
