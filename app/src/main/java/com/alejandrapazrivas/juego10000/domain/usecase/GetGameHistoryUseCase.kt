package com.alejandrapazrivas.juego10000.domain.usecase

import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.repository.GameRepository
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obtener el historial de partidas.
 */
class GetGameHistoryUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository
) {
    companion object {
        const val MODE_SINGLE_PLAYER = "SINGLE_PLAYER"
    }
    
    /**
     * Obtiene el historial de partidas completadas con información de jugadores
     * @return Flow con lista de pares (partida, jugador ganador)
     */
    operator fun invoke(): Flow<List<Pair<Game, Player?>>> {
        val gamesFlow = gameRepository.getRecentCompletedGames()
        val playersFlow = playerRepository.getAllActivePlayers()

        return combine(gamesFlow, playersFlow) { games, players ->
            games
                .filter { game -> game.gameMode != MODE_SINGLE_PLAYER }
                .map { game ->
                    val winner = game.winnerPlayerId?.let { winnerId ->
                        players.find { it.id == winnerId }
                    }
                    Pair(game, winner)
                }
        }
    }

    /**
     * Obtiene el historial de partidas completadas de un jugador específico
     * @param playerId ID del jugador
     * @return Flow con lista de partidas completadas del jugador
     */
    fun getPlayerGameHistory(playerId: Long): Flow<List<Game>> {
        return gameRepository.getPlayerGames(playerId)
            .map { games -> 
                games.filter { game -> 
                    game.gameMode != MODE_SINGLE_PLAYER && game.isCompleted 
                } 
            }
    }
}
