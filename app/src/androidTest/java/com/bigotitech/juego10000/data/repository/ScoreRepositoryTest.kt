package com.bigotitech.juego10000.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.juego10000.data.local.AppDatabase
import com.bigotitech.juego10000.data.local.dao.GameDao
import com.bigotitech.juego10000.data.local.dao.PlayerDao
import com.bigotitech.juego10000.data.local.dao.ScoreDao
import com.bigotitech.juego10000.data.local.entity.GameEntity
import com.bigotitech.juego10000.data.local.entity.PlayerEntity
import com.bigotitech.juego10000.data.local.entity.ScoreEntity
import com.bigotitech.juego10000.domain.model.Score
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
 * Pruebas instrumentadas para el repositorio de puntuaciones.
 */
@RunWith(AndroidJUnit4::class)
class ScoreRepositoryTest {
    private lateinit var scoreDao: ScoreDao
    private lateinit var gameDao: GameDao
    private lateinit var playerDao: PlayerDao
    private lateinit var scoreRepository: ScoreRepositoryImpl
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
        scoreRepository = ScoreRepositoryImpl(scoreDao)

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
    fun saveAndGetScore() = runBlocking {
        val score = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500,
            diceRolls = 3,
            timestamp = Date()
        )

        val scoreEntity = ScoreEntity(
            scoreId = score.id,
            gameId = score.gameId,
            playerId = score.playerId,
            round = score.round,
            turnScore = score.turnScore,
            totalScore = score.totalScore,
            diceRolls = score.diceRolls,
            timestamp = score.timestamp
        )
        val scoreId = scoreDao.insertScore(scoreEntity)

        val scores = scoreRepository.getScoresByGameId(testGameId).first()

        assertEquals(1, scores.size)
        val retrievedScore = scores[0]
        assertEquals(scoreId, retrievedScore.id)
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
        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500,
            timestamp = Date(System.currentTimeMillis() - 2000)
        )
        val score2 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200,
            timestamp = Date(System.currentTimeMillis() - 1000)
        )
        val score3 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 3,
            turnScore = 300,
            totalScore = 1500,
            timestamp = Date()
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        val scoreEntity3 = ScoreEntity(
            scoreId = score3.id,
            gameId = score3.gameId,
            playerId = score3.playerId,
            round = score3.round,
            turnScore = score3.turnScore,
            totalScore = score3.totalScore,
            diceRolls = score3.diceRolls,
            timestamp = score3.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)
        scoreDao.insertScore(scoreEntity3)

        val scores = scoreRepository.getScoresByGameId(testGameId).first()

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

        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500,
            timestamp = Date(System.currentTimeMillis() - 2000)
        )
        val score2 = Score(
            id = 0,
            gameId = game2Id,
            playerId = testPlayerId,
            round = 1,
            turnScore = 700,
            totalScore = 700,
            timestamp = Date(System.currentTimeMillis() - 1000)
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)

        val scores = scoreRepository.getScoresByPlayerId(testPlayerId).first()

        assertEquals(2, scores.size)
        assertTrue(scores[0].timestamp.time >= scores[1].timestamp.time)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerGameScores() = runBlocking {
        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)

        val scores = scoreRepository.getPlayerGameScores(testGameId, testPlayerId).first()

        assertEquals(2, scores.size)
        assertEquals(1, scores[0].round)
        assertEquals(2, scores[1].round)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerTotalScore() = runBlocking {
        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)

        val totalScore = scoreRepository.getPlayerTotalScore(testGameId, testPlayerId)

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

        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )
        val score3 = Score(
            id = 0,
            gameId = game2Id,
            playerId = testPlayerId,
            round = 1,
            turnScore = 900,
            totalScore = 900
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        val scoreEntity3 = ScoreEntity(
            scoreId = score3.id,
            gameId = score3.gameId,
            playerId = score3.playerId,
            round = score3.round,
            turnScore = score3.turnScore,
            totalScore = score3.totalScore,
            diceRolls = score3.diceRolls,
            timestamp = score3.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)
        scoreDao.insertScore(scoreEntity3)

        val highestScore = scoreRepository.getPlayerHighestScore(testPlayerId)

        assertNotNull(highestScore)
        assertEquals(1200, highestScore)
    }

    @Test
    @Throws(Exception::class)
    fun getPlayerAverageScore() = runBlocking {
        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)

        val averageScore = scoreRepository.getPlayerAverageScore(testPlayerId)

        assertNotNull(averageScore)
        assertEquals(600f, averageScore)
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

        val score1 = Score(
            id = 0,
            gameId = completedGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = Score(
            id = 0,
            gameId = completedGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 900,
            totalScore = 1400
        )
        val score3 = Score(
            id = 0,
            gameId = completedGameId,
            playerId = testPlayerId,
            round = 3,
            turnScore = 700,
            totalScore = 2100
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        val scoreEntity3 = ScoreEntity(
            scoreId = score3.id,
            gameId = score3.gameId,
            playerId = score3.playerId,
            round = score3.round,
            turnScore = score3.turnScore,
            totalScore = score3.totalScore,
            diceRolls = score3.diceRolls,
            timestamp = score3.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)
        scoreDao.insertScore(scoreEntity3)

        val bestTurn = scoreRepository.getPlayerBestTurn(testPlayerId)

        assertNotNull(bestTurn)
        assertEquals(900, bestTurn?.turnScore)
        assertEquals(2, bestTurn?.round)
    }

    @Test
    @Throws(Exception::class)
    fun deleteGameScores() = runBlocking {
        val score1 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 1,
            turnScore = 500,
            totalScore = 500
        )
        val score2 = Score(
            id = 0,
            gameId = testGameId,
            playerId = testPlayerId,
            round = 2,
            turnScore = 700,
            totalScore = 1200
        )

        val scoreEntity1 = ScoreEntity(
            scoreId = score1.id,
            gameId = score1.gameId,
            playerId = score1.playerId,
            round = score1.round,
            turnScore = score1.turnScore,
            totalScore = score1.totalScore,
            diceRolls = score1.diceRolls,
            timestamp = score1.timestamp
        )
        val scoreEntity2 = ScoreEntity(
            scoreId = score2.id,
            gameId = score2.gameId,
            playerId = score2.playerId,
            round = score2.round,
            turnScore = score2.turnScore,
            totalScore = score2.totalScore,
            diceRolls = score2.diceRolls,
            timestamp = score2.timestamp
        )
        scoreDao.insertScore(scoreEntity1)
        scoreDao.insertScore(scoreEntity2)

        var scores = scoreRepository.getScoresByGameId(testGameId).first()
        assertEquals(2, scores.size)

        scoreRepository.deleteGameScores(testGameId)

        scores = scoreRepository.getScoresByGameId(testGameId).first()
        assertEquals(0, scores.size)
    }
}
