package com.alejandrapazrivas.juego10000.data.repository

import com.alejandrapazrivas.juego10000.data.local.dao.PlayerDao
import com.alejandrapazrivas.juego10000.data.local.dao.ScoreDao
import com.alejandrapazrivas.juego10000.data.local.entity.PlayerEntity
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    private val scoreDao: ScoreDao
) : PlayerRepository {

    override suspend fun createPlayer(name: String, avatarResourceId: Int): Long {
        val playerEntity = PlayerEntity(
            name = name,
            avatarResourceId = avatarResourceId
        )
        return playerDao.insertPlayer(playerEntity)
    }

    override suspend fun updatePlayer(player: Player) {
        val playerEntity = PlayerEntity(
            playerId = player.id,
            name = player.name,
            avatarResourceId = player.avatarResourceId,
            gamesPlayed = player.gamesPlayed,
            gamesWon = player.gamesWon,
            highestScore = player.highestScore,
            totalScore = player.totalScore,
            isActive = player.isActive,
            createdAt = player.createdAt
        )
        playerDao.updatePlayer(playerEntity)
    }

    override suspend fun deletePlayer(playerId: Long) {
        val player = playerDao.getPlayerById(playerId) ?: return
        playerDao.deletePlayer(player)
    }

    override suspend fun deactivatePlayer(playerId: Long) {
        val player = playerDao.getPlayerById(playerId) ?: return
        playerDao.updatePlayer(player.copy(isActive = false))
    }

    override fun getAllActivePlayers(): Flow<List<Player>> {
        return playerDao.getAllActivePlayers().map { list -> list.map { mapEntityToPlayer(it) } }
    }

    override fun getAllPlayers(): Flow<List<Player>> {
        return playerDao.getAllPlayers().map { list -> list.map { mapEntityToPlayer(it) } }
    }

    override suspend fun getPlayerById(playerId: Long): Player? {
        val playerEntity = playerDao.getPlayerById(playerId) ?: return null
        return mapEntityToPlayer(playerEntity)
    }

    override fun getPlayerByIdFlow(playerId: Long): Flow<Player?> {
        return playerDao.getPlayerByIdFlow(playerId).map { it?.let { mapEntityToPlayer(it) } }
    }

    override suspend fun getPlayerAverageScore(playerId: Long): Float {
        return scoreDao.getPlayerAverageScore(playerId) ?: 0f
    }

    override suspend fun getPlayerBestTurnScore(playerId: Long): Int {
        return scoreDao.getPlayerBestTurn(playerId)?.turnScore ?: 0
    }

    private fun mapEntityToPlayer(entity: PlayerEntity): Player {
        return Player(
            id = entity.playerId,
            name = entity.name,
            avatarResourceId = entity.avatarResourceId,
            gamesPlayed = entity.gamesPlayed,
            gamesWon = entity.gamesWon,
            highestScore = entity.highestScore,
            totalScore = entity.totalScore,
            isActive = entity.isActive,
            createdAt = entity.createdAt
        )
    }
}