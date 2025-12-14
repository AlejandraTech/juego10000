package com.bigotitech.rokub10000.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.rokub10000.data.local.AppDatabase
import com.bigotitech.rokub10000.data.local.entity.GameEntity
import com.bigotitech.rokub10000.data.local.entity.PlayerEntity
import com.bigotitech.rokub10000.data.local.entity.ScoreEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

/**
 * Pruebas instrumentadas para el DAO de puntuaciones.
 */
@RunWith(AndroidJUnit4::class)
class ScoreDaoTest {
    private lateinit var scoreDao: ScoreDao
    private lateinit var gameDao: GameDao
    private lateinit var playerDao: PlayerDao
    private lateinit var db: AppDatabase
    private var testGameId: Long = 0
    private var testPlayerId: Long = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        scoreDao = db.scoreDao()
        gameDao = db.gameDao()
        playerDao = db.playerDao()

        val player = PlayerEntity(name = "Jugador Test")
        testPlayerId = playerDao.insertPlayer(player)

        val game = GameEntity(
            playerIds = listOf(testPlayerId),
            gameMode = "MULTIPLAYER"
        )
        testGameId = gameDao.insertGame(game)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetScore() = runBlocking {
        val score = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500,
            diceRolls = 3
        )

        val scoreId = scoreDao.insertScore(score)

        val scores = scoreDao.getScoresByGameId(testGameId).first()

        assertEquals(1, scores.size)
        val retrievedScore = scores[0]
        assertEquals(scoreId, retrievedScore.scoreId)
        assertEquals(testGameId, retrievedScore.gameId)
        assertEquals(testPlayerId, retrievedScore.playerId)
        assertEquals(1, retrievedScore.round)
        assertEquals(500, retrievedScore.turnScore)
        assertEquals(500, retrievedScore.totalScore)
        assertEquals(3, retrievedScore.diceRolls)
    }

    @Test
    @Throws(Exception::class)
    fun getScoresByGameId() = runBlocking {
        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500,
            timestamp = Date(System.currentTimeMillis() - 2000)
        )
        val score2 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200,
            timestamp = Date(System.currentTimeMillis() - 1000)
        )
        val score3 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 3,
            turnScore = 300,
            totalScore = 1500,
            timestamp = Date()
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)
        scoreDao.insertScore(score3)

        val scores = scoreDao.getScoresByGameId(testGameId).first()

        assertEquals(3, scores.size)
        assertEquals(1, scores[0].round)
        assertEquals(2, scores[1].round)
        assertEquals(3, scores[2].round)
    }

    @Test
    @Throws(Exception::class)
    fun getScoresByPlayerId() = runBlocking {
        val game2 = GameEntity(
            playerIds = listOf(testPlayerId),
            gameMode = "MULTIPLAYER"
        )
        val game2Id = gameDao.insertGame(game2)

        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500,
            timestamp = Date(System.currentTimeMillis() - 2000)
        )
        val score2 = ScoreEntity(
            gameId = game2Id,
            playerId = testPlayerId,
            round = 1,
            turnScore = 700,
            totalScore = 700,
            timestamp = Date(System.currentTimeMillis() - 1000)
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)

        val scores = scoreDao.getScoresByPlayerId(testPlayerId).first()

        assertEquals(2, scores.size)
        assertTrue(scores[0].timestamp.time >= scores[1].timestamp.time)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerGameScores() = runBlocking {
        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)

        val scores = scoreDao.getPlayerGameScores(testGameId, testPlayerId).first()

        assertEquals(2, scores.size)
        assertEquals(1, scores[0].round)
        assertEquals(2, scores[1].round)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerTotalScore() = runBlocking {
        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)

        val totalScore = scoreDao.getPlayerTotalScore(testGameId, testPlayerId)

        assertNotNull(totalScore)
        assertEquals(1200, totalScore)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerHighestScore() = runBlocking {
        val game2 = GameEntity(
            playerIds = listOf(testPlayerId),
            gameMode = "MULTIPLAYER"
        )
        val game2Id = gameDao.insertGame(game2)

        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )
        val score3 = ScoreEntity(
            gameId = game2Id,
            playerId = testPlayerId,
            round = 1,
            turnScore = 900,
            totalScore = 900
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)
        scoreDao.insertScore(score3)

        val highestScore = scoreDao.getPlayerHighestScore(testPlayerId)

        assertNotNull(highestScore)
        assertEquals(1200, highestScore)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerAverageScore() = runBlocking {
        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)

        val averageScore = scoreDao.getPlayerAverageScore(testPlayerId)

        assertNotNull(averageScore)
        assertEquals(600f, averageScore!!, 0.01f)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerBestTurn() = runBlocking {
        val completedGame = GameEntity(
            playerIds = listOf(testPlayerId),
            isCompleted = true,
            completedAt = Date(),
            gameMode = "MULTIPLAYER"
        )
        val completedGameId = gameDao.insertGame(completedGame)

        val score1 = ScoreEntity(
            gameId = completedGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = ScoreEntity(
            gameId = completedGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 900,
            totalScore = 1400
        )
        val score3 = ScoreEntity(
            gameId = completedGameId,
            playerId = testPlayerId,
            round = 3,
            turnScore = 700,
            totalScore = 2100
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)
        scoreDao.insertScore(score3)

        val bestTurn = scoreDao.getPlayerBestTurn(testPlayerId)

        assertNotNull(bestTurn)
        assertEquals(900, bestTurn?.turnScore)
        assertEquals(2, bestTurn?.round)
    }

    @Test
    @Throws(Exception::class)
    fun deleteGameScores() = runBlocking {
        val score1 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = ScoreEntity(
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        scoreDao.insertScore(score1)
        scoreDao.insertScore(score2)

        var scores = scoreDao.getScoresByGameId(testGameId).first()
        assertEquals(2, scores.size)

        scoreDao.deleteGameScores(testGameId)

        scores = scoreDao.getScoresByGameId(testGameId).first()
        assertEquals(0, scores.size)
    }
}
