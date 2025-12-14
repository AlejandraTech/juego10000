package com.bigotitech.juego10000.domain.usecase

import com.bigotitech.juego10000.domain.model.Player
import com.bigotitech.juego10000.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Caso de uso para gestionar jugadores (crear, actualizar, eliminar).
 */
class ManagePlayersUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    /**
     * Crea un nuevo jugador
     * @param name Nombre del jugador
     * @param avatarResourceId ID del recurso de avatar (opcional)
     * @return ID del jugador creado
     */
    suspend fun createPlayer(name: String, avatarResourceId: Int = 0): Result<Long> {
        return try {
            validatePlayerName(name)?.let { return Result.failure(it) }

            val playerId = playerRepository.createPlayer(
                name = name.trim(),
                avatarResourceId = avatarResourceId
            )

            Result.success(playerId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un jugador existente
     * @param player Objeto jugador con los datos actualizados
     */
    suspend fun updatePlayer(player: Player): Result<Unit> {
        return try {
            validatePlayerName(player.name)?.let { return Result.failure(it) }

            playerRepository.updatePlayer(player.copy(name = player.name.trim()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Desactiva un jugador (no lo elimina)
     * @param playerId ID del jugador a desactivar
     */
    suspend fun deactivatePlayer(playerId: Long): Result<Unit> {
        return try {
            playerRepository.deactivatePlayer(playerId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina permanentemente un jugador
     * @param playerId ID del jugador a eliminar
     */
    suspend fun deletePlayer(playerId: Long): Result<Unit> {
        return try {
            playerRepository.deletePlayer(playerId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene estadísticas detalladas de un jugador
     * @param playerId ID del jugador
     * @return Mapa con estadísticas del jugador
     */
    suspend fun getPlayerStats(playerId: Long): Result<Map<String, Any>> {
        return try {
            val player = playerRepository.getPlayerById(playerId)
                ?: return Result.failure(IllegalArgumentException("Jugador no encontrado"))

            val averageScore = playerRepository.getPlayerAverageScore(playerId)
            val bestTurnScore = playerRepository.getPlayerBestTurnScore(playerId)

            val stats = mapOf(
                "gamesPlayed" to player.gamesPlayed,
                "gamesWon" to player.gamesWon,
                "winRate" to player.winRate,
                "highestScore" to player.highestScore,
                "averageScore" to averageScore,
                "bestTurnScore" to bestTurnScore,
                "totalScore" to player.totalScore
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Valida el nombre del jugador
     * @param name Nombre a validar
     * @return Excepción si el nombre no es válido, null si es válido
     */
    private fun validatePlayerName(name: String): Exception? {
        return if (name.isBlank()) {
            IllegalArgumentException("El nombre del jugador no puede estar vacío")
        } else {
            null
        }
    }
}
