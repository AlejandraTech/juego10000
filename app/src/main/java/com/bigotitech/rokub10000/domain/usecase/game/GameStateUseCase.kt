package com.bigotitech.rokub10000.domain.usecase.game

import com.bigotitech.rokub10000.domain.model.Dice
import com.bigotitech.rokub10000.domain.model.Game
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.repository.GameRepository
import com.bigotitech.rokub10000.domain.repository.PlayerRepository
import com.bigotitech.rokub10000.domain.repository.ScoreRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obtener y gestionar el estado de una partida.
 */
class GameStateUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val scoreRepository: ScoreRepository,
    private val gson: Gson
) {
    /**
     * Obtiene el estado completo de una partida en curso
     * @param gameId ID de la partida
     * @return Flow con el estado completo de la partida
     */
    fun getGameState(gameId: Long): Flow<GameState?> {
        val gameFlow = gameRepository.getGameByIdFlow(gameId)
        val playersFlow = playerRepository.getAllPlayers()

        return combine(gameFlow, playersFlow) { game, allPlayers ->
            game?.let {
                val gameStateData = parseGameState(game.gameState)

                val gamePlayers = allPlayers.filter { player ->
                    game.playerIds.contains(player.id)
                }

                GameState(
                    game = game,
                    players = gamePlayers,
                    currentPlayerIndex = game.currentPlayerIndex,
                    currentRound = game.currentRound,
                    additionalState = gameStateData
                )
            }
        }
    }

    /**
     * Parsea el estado del juego desde una cadena JSON o formato simple
     */
    private fun parseGameState(gameState: String?): Map<String, Any> {
        if (gameState == null) return emptyMap()

        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(gameState, type)
        } catch (e: Exception) {
            gameState.split(";")
                .filter { it.contains("=") }
                .associate {
                    val parts = it.split("=", limit = 2)
                    parts[0] to parts[1]
                }
        }
    }

    /**
     * Obtiene las puntuaciones actuales de todos los jugadores en una partida
     * @param gameId ID de la partida
     * @return Flow con mapa de ID de jugador a puntuación total
     */
    fun getGameScores(gameId: Long): Flow<Map<Long, Int>> {
        return scoreRepository.getScoresByGameId(gameId)
            .map { scores ->
                scores.groupBy { it.playerId }
                    .mapValues { (_, playerScores) ->
                        playerScores.maxByOrNull { it.totalScore }?.totalScore ?: 0
                    }
            }
    }

    /**
     * Clase de datos que representa el estado completo de una partida
     */
    data class GameState(
        val game: Game,
        val players: List<Player>,
        val currentPlayerIndex: Int,
        val currentRound: Int,
        val additionalState: Map<String, Any> = emptyMap()
    ) {
        /**
         * Obtiene el jugador actual
         */
        val currentPlayer: Player?
            get() = players.getOrNull(currentPlayerIndex)

        /**
         * Indica si la partida ha terminado
         */
        val isGameOver: Boolean
            get() = game.isCompleted

        /**
         * Obtiene el jugador ganador (si existe)
         */
        val winner: Player?
            get() = game.winnerPlayerId?.let { winnerId ->
                players.find { it.id == winnerId }
            }

        /**
         * Obtiene la lista de dados del estado actual
         */
        @Suppress("UNCHECKED_CAST")
        fun getDice(): List<Dice> {
            return (additionalState["dice"] as? List<Map<String, Any>>)?.map {
                Dice(
                    id = (it["id"] as Double).toInt(),
                    value = (it["value"] as Double).toInt(),
                    isSelected = it["isSelected"] as Boolean,
                    isLocked = it["isLocked"] as Boolean
                )
            } ?: emptyList()
        }

        /**
         * Obtiene la puntuación del turno actual
         */
        @Suppress("UNCHECKED_CAST")
        fun getCurrentTurnScore(): Int {
            return (additionalState["currentTurnScore"] as? Double)?.toInt() ?: 0
        }
    }
}
