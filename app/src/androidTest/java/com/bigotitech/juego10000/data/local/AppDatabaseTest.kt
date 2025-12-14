package com.bigotitech.juego10000.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.juego10000.data.local.dao.GameDao
import com.bigotitech.juego10000.data.local.dao.PlayerDao
import com.bigotitech.juego10000.data.local.dao.ScoreDao
import com.bigotitech.juego10000.data.local.entity.GameEntity
import com.bigotitech.juego10000.data.local.entity.PlayerEntity
import com.bigotitech.juego10000.data.local.entity.ScoreEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

/**
 * Pruebas instrumentadas para la base de datos completa.
 * Verifica la integración entre las diferentes entidades y DAOs.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var playerDao: PlayerDao
    private lateinit var gameDao: GameDao
    private lateinit var scoreDao: ScoreDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        playerDao = db.playerDao()
        gameDao = db.gameDao()
        scoreDao = db.scoreDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testDatabaseIntegration() = runBlocking {
        val player1 = PlayerEntity(name = "Jugador 1")
        val player2 = PlayerEntity(name = "Jugador 2")

        val player1Id = playerDao.insertPlayer(player1)
        val player2Id = playerDao.insertPlayer(player2)

        val allPlayers = playerDao.getAllPlayers().first()
        assertEquals(2, allPlayers.size)

        val game = GameEntity(
            playerIds = listOf(player1Id, player2Id),
            targetScore = 10000,
            isCompleted = false,
            currentPlayerIndex = 0,
            currentRound = 1,
            gameMode = "MULTIPLAYER"
        )

        val gameId = gameDao.insertGame(game)

        val retrievedGame = gameDao.getGameById(gameId)
        assertNotNull(retrievedGame)
        assertEquals(listOf(player1Id, player2Id), retrievedGame?.playerIds)

        val score1 = ScoreEntity(
            gameId = gameId,
            playerId = player1Id,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )

        val score2 = ScoreEntity(
            gameId = gameId,
            playerId = player2Id,
            round = 1,
            turnScore = 700,
            totalScore = 700
        )

        val score3 = ScoreEntity(
            gameId = gameId,
            playerId = player1Id,
            round = 2,
            turnScore = 800,
            totalScore = 1300
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)
        scoreDao.insertScore(score3)

        val gameScores = scoreDao.getScoresByGameId(gameId).first()
        assertEquals(3, gameScores.size)

        gameDao.completeGame(gameId, player1Id, Date())

        val completedGame = gameDao.getGameById(gameId)
        assertEquals(true, completedGame?.isCompleted)
        assertEquals(player1Id, completedGame?.winnerPlayerId)

        playerDao.incrementGamesPlayed(listOf(player1Id, player2Id))
        playerDao.incrementGamesWon(player1Id)
        playerDao.updateHighestScore(player1Id, 1300)
        playerDao.updateHighestScore(player2Id, 700)

        val updatedPlayer1 = playerDao.getPlayerById(player1Id)
        val updatedPlayer2 = playerDao.getPlayerById(player2Id)

        assertEquals(1, updatedPlayer1?.gamesPlayed)
        assertEquals(1, updatedPlayer1?.gamesWon)
        assertEquals(1300, updatedPlayer1?.highestScore)

        assertEquals(1, updatedPlayer2?.gamesPlayed)
        assertEquals(0, updatedPlayer2?.gamesWon)
        assertEquals(700, updatedPlayer2?.highestScore)

        val player1Games = gameDao.getPlayerGames(player1Id).first()
        assertEquals(1, player1Games.size)
        assertEquals(gameId, player1Games[0].gameId)

        val player1Scores = scoreDao.getScoresByPlayerId(player1Id).first()
        assertEquals(2, player1Scores.size)

        gameDao.deleteGame(gameId)

        val deletedGame = gameDao.getGameById(gameId)
        assertEquals(null, deletedGame)

        val deletedGameScores = scoreDao.getScoresByGameId(gameId).first()
        assertEquals(0, deletedGameScores.size)
    }

    @Test
    @Throws(Exception::class)
    fun testTypeConverters() = runBlocking {
        val now = Date()

        val game = GameEntity(
            playerIds = listOf(1L, 2L),
            startedAt = now,
            gameMode = "MULTIPLAYER"
        )

        val gameId = gameDao.insertGame(game)
        val retrievedGame = gameDao.getGameById(gameId)

        assertNotNull(retrievedGame?.startedAt)
        assertEquals(now.time / 1000, retrievedGame?.startedAt?.time!! / 1000)

        val playerIds = listOf(10L, 20L, 30L)

        val gameWithList = GameEntity(
            playerIds = playerIds,
            gameMode = "MULTIPLAYER"
        )

        val gameWithListId = gameDao.insertGame(gameWithList)
        val retrievedGameWithList = gameDao.getGameById(gameWithListId)

        assertEquals(playerIds, retrievedGameWithList?.playerIds)
        assertEquals(3, retrievedGameWithList?.playerIds?.size)
    }
}
