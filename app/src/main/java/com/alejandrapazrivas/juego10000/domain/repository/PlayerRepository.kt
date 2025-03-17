package com.alejandrapazrivas.juego10000.domain.repository

import com.alejandrapazrivas.juego10000.domain.model.Player
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar las operaciones relacionadas con jugadores.
 */
interface PlayerRepository {
    /**
     * Crea un nuevo jugador.
     * 
     * @param name Nombre del jugador
     * @param avatarResourceId ID del recurso de avatar del jugador
     * @return ID del jugador creado
     */
    suspend fun createPlayer(name: String, avatarResourceId: Int = 0): Long
    
    /**
     * Actualiza los datos de un jugador existente.
     * 
     * @param player Objeto Player con los datos actualizados
     */
    suspend fun updatePlayer(player: Player)
    
    /**
     * Elimina un jugador de la base de datos.
     * 
     * @param playerId ID del jugador a eliminar
     */
    suspend fun deletePlayer(playerId: Long)
    
    /**
     * Desactiva un jugador (lo marca como inactivo).
     * 
     * @param playerId ID del jugador a desactivar
     */
    suspend fun deactivatePlayer(playerId: Long)
    
    /**
     * Obtiene todos los jugadores activos.
     * 
     * @return Flujo que emite la lista de jugadores activos
     */
    fun getAllActivePlayers(): Flow<List<Player>>
    
    /**
     * Obtiene todos los jugadores (activos e inactivos).
     * 
     * @return Flujo que emite la lista de todos los jugadores
     */
    fun getAllPlayers(): Flow<List<Player>>
    
    /**
     * Obtiene un jugador por su ID.
     * 
     * @param playerId ID del jugador a obtener
     * @return El jugador si existe, o null si no se encuentra
     */
    suspend fun getPlayerById(playerId: Long): Player?
    
    /**
     * Obtiene un jugador por su ID como un flujo observable.
     * 
     * @param playerId ID del jugador a obtener
     * @return Flujo que emite el jugador si existe, o null si no se encuentra
     */
    fun getPlayerByIdFlow(playerId: Long): Flow<Player?>
    
    /**
     * Obtiene la puntuación media de un jugador.
     * 
     * @param playerId ID del jugador
     * @return Puntuación media del jugador
     */
    suspend fun getPlayerAverageScore(playerId: Long): Float
    
    /**
     * Obtiene la puntuación más alta de un turno para un jugador.
     * 
     * @param playerId ID del jugador
     * @return Puntuación más alta de un turno
     */
    suspend fun getPlayerBestTurnScore(playerId: Long): Int
}
