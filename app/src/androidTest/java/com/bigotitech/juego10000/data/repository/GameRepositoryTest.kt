package com.bigotitech.juego10000.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.juego10000.data.local.AppDatabase
import com.bigotitech.juego10000.data.local.dao.GameDao
import com.bigotitech.juego10000.data.local.dao.PlayerDao
import com.bigotitech.juego10000.data.local.dao.ScoreDao
import com.bigotitech.juego10000.data.local.entity.PlayerEntity
import com.bigotitech.juego10000.data.local.entity.ScoreEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Pruebas instrumentadas para el repositorio de juegos.
 */
@RunWith(AndroidJUnit4::class)
class GameRepositoryTest {
    private lateinit var gameDao: GameDao
    private lateinit var playerDao: PlayerDao
    private lateinit var scoreDao: ScoreDao
    private lateinit var gameRepository: GameRepositoryImpl
    private lateinit var db: AppDatabase
    private lateinit var testPlayerIds: List<Long>

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        gameDao = db.gameDao()
        playerDao = db.playerDao()
        scoreDao = db.scoreDao()
        gameRepository = GameRepositoryImpl(gameDao, playerDao, scoreDao)

        val player1 = PlayerEntity(name = "Jugador 1")
        val player2 = PlayerEntity(name = "Jugador 2")
        val player3 = PlayerEntity(name = "Jugador 3")

        val id1 = playerDao.insertPlayer(player1)
        val id2 = playerDao.insertPlayer(player2)
        val id3 = playerDao.insertPlayer(player3)

        testPlayerIds = listOf(id1, id2, id3)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetGame() = runBlocking {
        val gameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            targetScore = 10000,
            gameMode = "MULTIPLAYER"
        )

        val retrievedGame = gameRepository.getGameById(gameId)

        assertNotNull(retrievedGame)
        assertEquals(testPlayerIds, retrievedGame?.playerIds)
        assertEquals(10000, retrievedGame?.targetScore)
        assertEquals(false, retrievedGame?.isCompleted)
        assertEquals(0, retrievedGame?.currentPlayerIndex)
        assertEquals(1, retrievedGame?.currentRound)
        assertEquals("MULTIPLAYER", retrievedGame?.gameMode)
    }

    @Test
    @Throws(Exception::class)
    fun getActiveGames() = runBlocking {
        val activeGame1Id = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        val activeGame2Id = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "SINGLE_PLAYER"
        )

        val completedGameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        gameRepository.completeGame(completedGameId, testPlayerIds[0])

        val activeGames = gameRepository.getActiveGames().first()

        assertEquals(2, activeGames.size)
        assertEquals(false, activeGames.all { it.isCompleted })
    }

    @Test
    @Throws(Exception::class)
    fun getRecentCompletedGames() = runBlocking {
        // Crear e insertar juegos completados y activos
        val completedGame1Id = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        val completedGame2Id = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "SINGLE_PLAYER"
        )

        val activeGameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        gameRepository.completeGame(completedGame1Id, testPlayerIds[0])
        Thread.sleep(1000)
        gameRepository.completeGame(completedGame2Id, testPlayerIds[1])

        val completedGames = gameRepository.getRecentCompletedGames().first()

        assertEquals(2, completedGames.size)
        assertEquals(true, completedGames.all { it.isCompleted })

        assertTrue(completedGames[0].completedAt!!.time >= completedGames[1].completedAt!!.time)
    }

    @Test
    @Throws(Exception::class)
    fun completeGame() = runBlocking {
        val gameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        // Completar el juego
        val winnerId = testPlayerIds[1]
        gameRepository.completeGame(gameId, winnerId)

        val completedGame = gameRepository.getGameById(gameId)

        assertNotNull(completedGame)
        assertEquals(true, completedGame?.isCompleted)
        assertEquals(winnerId, completedGame?.winnerPlayerId)
        assertNotNull(completedGame?.completedAt)
    }

    @Test
    @Throws(Exception::class)
    fun updateGameState() = runBlocking {
        val gameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        val newPlayerIndex = 2
        val newRound = 3
        val newGameState = """{"dice":[1,3,5,2,4,6],"selectedDice":[0,2],"score":150}"""

        gameRepository.updateGameState(gameId, newPlayerIndex, newRound, newGameState)

        val updatedGame = gameRepository.getGameById(gameId)

        assertNotNull(updatedGame)
        assertEquals(newPlayerIndex, updatedGame?.currentPlayerIndex)
        assertEquals(newRound, updatedGame?.currentRound)
        assertEquals(newGameState, updatedGame?.gameState)
    }

    @Test
    @Throws(Exception::class)
    fun deleteGame() = runBlocking {
        val gameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        val insertedGame = gameRepository.getGameById(gameId)
        assertNotNull(insertedGame)

        gameRepository.deleteGame(gameId)

        val deletedGame = gameRepository.getGameById(gameId)
        assertNull(deletedGame)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerGames() = runBlocking {
        val specialPlayer = PlayerEntity(name = "Jugador Especial")
        val specialPlayerId = playerDao.insertPlayer(specialPlayer)

        val game1Id = gameRepository.createGame(
            playerIds = listOf(specialPlayerId, testPlayerIds[0]),
            gameMode = "MULTIPLAYER"
        )

        val game2Id = gameRepository.createGame(
            playerIds = listOf(specialPlayerId, testPlayerIds[1]),
            gameMode = "MULTIPLAYER"
        )

        gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        gameRepository.completeGame(game1Id, specialPlayerId)
        Thread.sleep(1000)
        gameRepository.completeGame(game2Id, specialPlayerId)

        scoreDao.insertScore(
            ScoreEntity(
                gameId = game1Id,
                playerId = specialPlayerId,
                round = 1,
                turnScore = 500,
                totalScore = 500
            )
        )
        scoreDao.insertScore(
            ScoreEntity(
                gameId = game2Id,
                playerId = specialPlayerId,
                round = 1,
                turnScore = 700,
                totalScore = 700
            )
        )

        val playerGames = gameRepository.getPlayerGames(specialPlayerId).first()

        assertEquals(2, playerGames.size)
        assertTrue(playerGames.all { it.isCompleted })

        assertTrue(playerGames[0].completedAt!!.time >= playerGames[1].completedAt!!.time)
    }

    @Test
    @Throws(Exception::class)
    fun saveScore() = runBlocking {
        val gameId = gameRepository.createGame(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )

        val scoreId = gameRepository.saveScore(
            gameId = gameId,
            playerId = testPlayerIds[0],
            round = 1,
            turnScore = 500,
            totalScore = 500,
            diceRolls = 3
        )

        val scores = scoreDao.getScoresByGameId(gameId).first()
        assertEquals(1, scores.size)
        assertEquals(500, scores[0].turnScore)
        assertEquals(500, scores[0].totalScore)
        assertEquals(3, scores[0].diceRolls)

        val player = playerDao.getPlayerById(testPlayerIds[0])
        assertEquals(500, player?.highestScore)
        assertEquals(500L, player?.totalScore)
    }
}
