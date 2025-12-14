package com.bigotitech.juego10000.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.juego10000.data.local.AppDatabase
import com.bigotitech.juego10000.data.local.entity.GameEntity
import com.bigotitech.juego10000.data.local.entity.PlayerEntity
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
import java.util.Date

/**
 * Pruebas instrumentadas para el DAO de juegos.
 */
@RunWith(AndroidJUnit4::class)
class GameDaoTest {
    private lateinit var gameDao: GameDao
    private lateinit var playerDao: PlayerDao
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
    fun insertAndGetGame() = runBlocking {
        val game = GameEntity(
            playerIds = testPlayerIds,
            targetScore = 10000,
            isCompleted = false,
            currentPlayerIndex = 0,
            currentRound = 1,
            gameMode = "MULTIPLAYER"
        )

        val gameId = gameDao.insertGame(game)

        val retrievedGame = gameDao.getGameById(gameId)

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
        val activeGame1 = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = false,
            gameMode = "MULTIPLAYER"
        )
        val activeGame2 = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = false,
            gameMode = "SINGLE_PLAYER"
        )
        val completedGame = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = true,
            gameMode = "MULTIPLAYER",
            completedAt = Date()
        )

        gameDao.insertGame(activeGame1)
        gameDao.insertGame(activeGame2)
        gameDao.insertGame(completedGame)

        val activeGames = gameDao.getActiveGames().first()

        assertEquals(2, activeGames.size)
        assertEquals(false, activeGames.all { it.isCompleted })
    }

    @Test
    @Throws(Exception::class)
    fun getRecentCompletedGames() = runBlocking {
        val completedGame1 = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = true,
            completedAt = Date(System.currentTimeMillis() - 1000),
            gameMode = "MULTIPLAYER"
        )
        val completedGame2 = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = true,
            completedAt = Date(),
            gameMode = "SINGLE_PLAYER"
        )
        val activeGame = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = false,
            gameMode = "MULTIPLAYER"
        )

        gameDao.insertGame(completedGame1)
        gameDao.insertGame(completedGame2)
        gameDao.insertGame(activeGame)

        val completedGames = gameDao.getRecentCompletedGames().first()

        assertEquals(2, completedGames.size)
        assertEquals(true, completedGames.all { it.isCompleted })

        assertTrue(completedGames[0].completedAt!!.time >= completedGames[1].completedAt!!.time)
    }

    @Test
    @Throws(Exception::class)
    fun completeGame() = runBlocking {
        val game = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = false,
            gameMode = "MULTIPLAYER"
        )
        val gameId = gameDao.insertGame(game)

        val winnerId = testPlayerIds[1]
        gameDao.completeGame(gameId, winnerId)

        val completedGame = gameDao.getGameById(gameId)

        assertNotNull(completedGame)
        assertEquals(true, completedGame?.isCompleted)
        assertEquals(winnerId, completedGame?.winnerPlayerId)
        assertNotNull(completedGame?.completedAt)
    }

    @Test
    @Throws(Exception::class)
    fun updateGameState() = runBlocking {
        val game = GameEntity(
            playerIds = testPlayerIds,
            currentPlayerIndex = 0,
            currentRound = 1,
            gameState = null,
            gameMode = "MULTIPLAYER"
        )
        val gameId = gameDao.insertGame(game)

        val newPlayerIndex = 2
        val newRound = 3
        val newGameState = """{"dice":[1,3,5,2,4,6],"selectedDice":[0,2],"score":150}"""

        gameDao.updateGameState(gameId, newPlayerIndex, newRound, newGameState)

        val updatedGame = gameDao.getGameById(gameId)

        assertNotNull(updatedGame)
        assertEquals(newPlayerIndex, updatedGame?.currentPlayerIndex)
        assertEquals(newRound, updatedGame?.currentRound)
        assertEquals(newGameState, updatedGame?.gameState)
    }

    @Test
    @Throws(Exception::class)
    fun deleteGame() = runBlocking {
        val game = GameEntity(
            playerIds = testPlayerIds,
            gameMode = "MULTIPLAYER"
        )
        val gameId = gameDao.insertGame(game)

        val insertedGame = gameDao.getGameById(gameId)
        assertNotNull(insertedGame)

        gameDao.deleteGame(gameId)

        val deletedGame = gameDao.getGameById(gameId)
        assertNull(deletedGame)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerGames() = runBlocking {
        val playerId = playerDao.insertPlayer(PlayerEntity(name = "Jugador Especial"))

        val game1 = GameEntity(
            playerIds = listOf(playerId, testPlayerIds[0]),
            isCompleted = true,
            completedAt = Date(System.currentTimeMillis() - 2000),
            gameMode = "MULTIPLAYER"
        )
        val game2 = GameEntity(
            playerIds = listOf(playerId, testPlayerIds[1]),
            isCompleted = true,
            completedAt = Date(System.currentTimeMillis() - 1000),
            gameMode = "MULTIPLAYER"
        )

        val game3 = GameEntity(
            playerIds = testPlayerIds,
            isCompleted = true,
            completedAt = Date(),
            gameMode = "MULTIPLAYER"
        )

        val gameId1 = gameDao.insertGame(game1)
        val gameId2 = gameDao.insertGame(game2)
        gameDao.insertGame(game3)

        val scoreDao = db.scoreDao()
        scoreDao.insertScore(
            com.bigotitech.juego10000.data.local.entity.ScoreEntity(
                gameId = gameId1,
                playerId = playerId,
                round = 1,
                turnScore = 500,
                totalScore = 500
            )
        )
        scoreDao.insertScore(
            com.bigotitech.juego10000.data.local.entity.ScoreEntity(
                gameId = gameId2,
                playerId = playerId,
                round = 1,
                turnScore = 700,
                totalScore = 700
            )
        )

        val playerGames = gameDao.getPlayerGames(playerId).first()

        assertEquals(2, playerGames.size)
        assertTrue(playerGames.all { it.isCompleted })

        assertTrue(playerGames[0].completedAt!!.time >= playerGames[1].completedAt!!.time)
    }
}