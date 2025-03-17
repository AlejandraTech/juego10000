package com.alejandrapazrivas.juego10000.domain.model

import com.alejandrapazrivas.juego10000.util.GameUtils
import kotlin.random.Random

/**
 * Modelo que representa un bot para el modo de juego individual.
 * 
 * Esta clase implementa diferentes estrategias de juego según el nivel de dificultad seleccionado,
 * permitiendo simular diferentes estilos de juego para oponentes controlados por la IA.
 *
 * @property difficulty Nivel de dificultad del bot que determina su estrategia
 * @property name Nombre del bot para mostrar en la interfaz
 */
class Bot(
    val difficulty: BotDifficulty,
    val name: String = "Bot"
) {
    /**
     * Probabilidad de que el bot tome riesgos según su nivel de dificultad.
     * A mayor dificultad, mayor probabilidad de tomar riesgos.
     */
    private val riskTakingProbability = when (difficulty) {
        BotDifficulty.BEGINNER -> 0.3f     // 30% de probabilidad de tomar riesgos
        BotDifficulty.INTERMEDIATE -> 0.5f // 50% de probabilidad de tomar riesgos
        BotDifficulty.EXPERT -> 0.7f       // 70% de probabilidad de tomar riesgos
    }

    /**
     * Puntuación mínima que el bot considerará para guardar según su nivel de dificultad.
     */
    private val minScoreToBank = when (difficulty) {
        BotDifficulty.BEGINNER -> 300      // Guarda puntos con facilidad
        BotDifficulty.INTERMEDIATE -> 450  // Más equilibrado
        BotDifficulty.EXPERT -> 600        // Busca puntuaciones más altas
    }

    /**
     * Puntuación objetivo para ganar la partida.
     */
    private val targetScore = 10000

    /**
     * Decide si el bot debe continuar lanzando dados o guardar su puntuación actual.
     * 
     * La decisión se basa en varios factores: la puntuación actual del turno, la puntuación total,
     * la puntuación máxima de los oponentes y el número de dados disponibles para lanzar.
     * La estrategia varía según el nivel de dificultad del bot.
     *
     * @param currentTurnScore Puntuación acumulada en el turno actual
     * @param totalScore Puntuación total del bot hasta el momento
     * @param opponentMaxScore Puntuación máxima entre todos los oponentes
     * @param availableDice Número de dados disponibles para lanzar
     * @return true si debe continuar lanzando, false si debe guardar los puntos
     */
    fun shouldContinueRolling(
        currentTurnScore: Int,
        totalScore: Int,
        opponentMaxScore: Int,
        availableDice: Int
    ): Boolean {
        // Si no ha entrado en juego y tiene menos de 500 puntos, debe seguir lanzando
        if (totalScore == 0 && currentTurnScore < 500) {
            return true
        }

        // Si está cerca de ganar, ajusta la estrategia
        val remainingPoints = targetScore - totalScore
        if (remainingPoints <= currentTurnScore) {
            // Si puede ganar exactamente, guarda los puntos
            return false
        }

        // Si el oponente está cerca de ganar, toma más riesgos
        if (opponentMaxScore > 8000) {
            return Random.nextFloat() < (riskTakingProbability + 0.2f)
        }

        // Estrategia basada en la puntuación actual del turno
        if (currentTurnScore >= minScoreToBank) {
            // Cuantos más puntos tenga, menos probable es que siga lanzando
            val baseChance = riskTakingProbability - (currentTurnScore / 2000f)
            
            // Ajuste por dados disponibles: con más dados, más probable es que siga
            val diceBonus = when {
                availableDice >= 5 -> 0.3f
                availableDice >= 3 -> 0.2f
                else -> 0.0f
            }
            
            return Random.nextFloat() < (baseChance + diceBonus).coerceIn(0.1f, 0.9f)
        }

        // Si tiene pocos puntos, sigue lanzando
        return true
    }

    /**
     * Decide qué dados seleccionar entre los disponibles según la estrategia del bot.
     * 
     * La selección varía según el nivel de dificultad:
     * - Principiante: selecciona la primera opción disponible
     * - Intermedio: selecciona aleatoriamente entre las mejores opciones
     * - Experto: evalúa estratégicamente considerando puntuación y dados restantes
     *
     * @param availableDice Lista de dados disponibles para seleccionar
     * @param scoringOptions Lista de posibles combinaciones de dados que puntúan
     * @return Lista de dados seleccionados por el bot
     */
    fun selectDice(
        availableDice: List<Dice>,
        scoringOptions: List<List<Dice>>
    ): List<Dice> {
        if (scoringOptions.isEmpty()) return emptyList()

        // Principiante: selecciona la primera opción disponible
        if (difficulty == BotDifficulty.BEGINNER) {
            return scoringOptions.first()
        }

        // Intermedio: selecciona aleatoriamente entre las opciones, con preferencia por las que dan más puntos
        if (difficulty == BotDifficulty.INTERMEDIATE) {
            val weightedOptions = scoringOptions.sortedByDescending { GameUtils.calculateScore(it) }
            val randomIndex = if (weightedOptions.size > 1) {
                Random.nextInt(0, minOf(2, weightedOptions.size))
            } else {
                0
            }
            return weightedOptions[randomIndex]
        }

        // Experto: evalúa estratégicamente las opciones
        // Prefiere opciones que maximizan la puntuación pero también dejan dados para seguir lanzando
        val scoredOptions = scoringOptions.map {
            val score = GameUtils.calculateScore(it)
            val remainingDice = availableDice.size - it.size
            // Bonificación por dejar dados disponibles
            val bonus = if (remainingDice > 0) remainingDice * 20 else 0
            Triple(it, score, score + bonus)
        }

        return scoredOptions.maxByOrNull { it.third }?.first ?: scoringOptions.first()
    }

}
