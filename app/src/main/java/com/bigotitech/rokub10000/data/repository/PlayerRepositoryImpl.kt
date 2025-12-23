package com.bigotitech.rokub10000.data.repository

import com.bigotitech.rokub10000.data.local.dao.PlayerDao
import com.bigotitech.rokub10000.data.local.dao.ScoreDao
import com.bigotitech.rokub10000.data.local.entity.PlayerEntity
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de jugadores.
 * Gestiona la creación, actualización y consulta de jugadores.
 */
class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    private val scoreDao: ScoreDao
) : BaseRepository(), PlayerRepository {

    /**
     * Crea un nuevo jugador en la base de datos.
     *
     * @param name Nombre del jugador
     * @param avatarResourceId ID del recurso de avatar del jugador
     * @param isBot Indica si el jugador es un bot (no aparece en estadísticas)
     * @return ID del jugador creado
     */
    override suspend fun createPlayer(name: String, avatarResourceId: Int, isBot: Boolean): Long {
        val playerEntity = PlayerEntity(
            name = name,
            avatarResourceId = avatarResourceId,
            isBot = isBot
        )
        return playerDao.insertPlayer(playerEntity)
    }

    /**
     * Actualiza los datos de un jugador existente.
     *
     * @param player Objeto Player con los datos actualizados
     */
    override suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(player.toEntity())
    }

    /**
     * Elimina un jugador de la base de datos.
     *
     * @param playerId ID del jugador a eliminar
     */
    override suspend fun deletePlayer(playerId: Long) {
        val player = playerDao.getPlayerById(playerId) ?: return
        playerDao.deletePlayer(player)
    }

    /**
     * Desactiva un jugador (lo marca como inactivo).
     *
     * @param playerId ID del jugador a desactivar
     */
    override suspend fun deactivatePlayer(playerId: Long) {
        val player = playerDao.getPlayerById(playerId) ?: return
        playerDao.updatePlayer(player.copy(isActive = false))
    }

    /**
     * Obtiene todos los jugadores activos.
     *
     * @return Flujo que emite la lista de jugadores activos
     */
    override fun getAllActivePlayers(): Flow<List<Player>> {
        return playerDao.getAllActivePlayers().map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene todos los jugadores (activos e inactivos).
     *
     * @return Flujo que emite la lista de todos los jugadores
     */
    override fun getAllPlayers(): Flow<List<Player>> {
        return playerDao.getAllPlayers().map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene un jugador por su ID.
     *
     * @param playerId ID del jugador a obtener
     * @return El jugador si existe, o null si no se encuentra
     */
    override suspend fun getPlayerById(playerId: Long): Player? {
        val playerEntity = playerDao.getPlayerById(playerId) ?: return null
        return playerEntity.toDomainModel()
    }

    /**
     * Obtiene un jugador por su ID como un flujo observable.
     *
     * @param playerId ID del jugador a obtener
     * @return Flujo que emite el jugador si existe, o null si no se encuentra
     */
    override fun getPlayerByIdFlow(playerId: Long): Flow<Player?> {
        return playerDao.getPlayerByIdFlow(playerId).map { it?.toDomainModel() }
    }

    /**
     * Obtiene la puntuación media de un jugador.
     *
     * @param playerId ID del jugador
     * @return Puntuación media del jugador
     */
    override suspend fun getPlayerAverageScore(playerId: Long): Float {
        return scoreDao.getPlayerAverageScore(playerId) ?: 0f
    }

    /**
     * Obtiene la puntuación más alta de un turno para un jugador.
     *
     * @param playerId ID del jugador
     * @return Puntuación más alta de un turno
     */
    override suspend fun getPlayerBestTurnScore(playerId: Long): Int {
        val bestTurn = scoreDao.getPlayerBestTurn(playerId)
        return bestTurn?.turnScore ?: 0
    }

    /**
     * Obtiene o crea un bot para la dificultad especificada.
     * Si ya existe un bot para esa dificultad, lo devuelve.
     * Si no existe, lo crea y lo devuelve.
     *
     * @param difficulty Nivel de dificultad del bot
     * @param botName Nombre del bot
     * @return ID del bot
     */
    override suspend fun getOrCreateBot(difficulty: String, botName: String): Long {
        val existingBot = playerDao.getBotByDifficulty(difficulty)
        if (existingBot != null) {
            return existingBot.playerId
        }

        val botEntity = PlayerEntity(
            name = botName,
            avatarResourceId = 0,
            isBot = true,
            botDifficulty = difficulty
        )
        return playerDao.insertPlayer(botEntity)
    }
}
