package com.bigotitech.rokub10000.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.rokub10000.data.local.AppDatabase
import com.bigotitech.rokub10000.data.local.dao.PlayerDao
import com.bigotitech.rokub10000.data.local.dao.ScoreDao
import com.bigotitech.rokub10000.domain.model.Player
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Pruebas instrumentadas para el repositorio de jugadores.
 */
@RunWith(AndroidJUnit4::class)
class PlayerRepositoryTest {
    private lateinit var playerDao: PlayerDao
    private lateinit var scoreDao: ScoreDao
    private lateinit var playerRepository: PlayerRepositoryImpl
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        playerDao = db.playerDao()
        scoreDao = db.scoreDao()
        playerRepository = PlayerRepositoryImpl(playerDao, scoreDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetPlayer() = runBlocking {
        val player = Player(
            id = 0,
            name = "Jugador Test",
            avatarResourceId = 123,
            gamesPlayed = 0,
            gamesWon = 0,
            highestScore = 0,
            totalScore = 0,
            isActive = true
        )

        val playerId = playerRepository.createPlayer(player.name, player.avatarResourceId)

        val retrievedPlayer = playerRepository.getPlayerById(playerId)

        assertNotNull(retrievedPlayer)
        assertEquals(player.name, retrievedPlayer?.name)
        assertEquals(player.avatarResourceId, retrievedPlayer?.avatarResourceId)
        assertEquals(player.isActive, retrievedPlayer?.isActive)
    }

    @Test
    @Throws(Exception::class)
    fun updatePlayer() = runBlocking {
        val player = Player(
            id = 0,
            name = "Jugador Original",
            avatarResourceId = 123,
            gamesPlayed = 0,
            gamesWon = 0,
            highestScore = 0,
            totalScore = 0,
            isActive = true
        )
        val playerId = playerRepository.createPlayer(player.name, player.avatarResourceId)

        val originalPlayer = playerRepository.getPlayerById(playerId)
        assertNotNull(originalPlayer)

        val updatedPlayer = originalPlayer!!.copy(
            name = "Jugador Actualizado",
            gamesPlayed = 5,
            gamesWon = 2,
            highestScore = 8000,
            totalScore = 25000
        )
        playerRepository.updatePlayer(updatedPlayer)

        val retrievedPlayer = playerRepository.getPlayerById(playerId)

        assertNotNull(retrievedPlayer)
        assertEquals("Jugador Actualizado", retrievedPlayer?.name)
        assertEquals(5, retrievedPlayer?.gamesPlayed)
        assertEquals(2, retrievedPlayer?.gamesWon)
        assertEquals(8000, retrievedPlayer?.highestScore)
        assertEquals(25000L, retrievedPlayer?.totalScore)
    }

    @Test
    @Throws(Exception::class)
    fun deletePlayer() = runBlocking {
        val player = Player(
            id = 0,
            name = "Jugador a Eliminar",
            avatarResourceId = 123,
            isActive = true
        )
        val playerId = playerRepository.createPlayer(player.name, player.avatarResourceId)

        val insertedPlayer = playerRepository.getPlayerById(playerId)
        assertNotNull(insertedPlayer)

        playerRepository.deletePlayer(insertedPlayer!!.id)

        val deletedPlayer = playerRepository.getPlayerById(playerId)
        assertNull(deletedPlayer)
    }

    @Test
    @Throws(Exception::class)
    fun getAllActivePlayers() = runBlocking {
        val player1 = Player(id = 0, name = "Jugador 1", isActive = true)
        val player2 = Player(id = 0, name = "Jugador 2", isActive = true)
        val player3 = Player(id = 0, name = "Jugador 3", isActive = false)

        playerRepository.createPlayer(player1.name, player1.avatarResourceId)
        playerRepository.createPlayer(player2.name, player2.avatarResourceId)
        playerRepository.createPlayer(player3.name, player3.avatarResourceId)

        val activePlayers = playerRepository.getAllActivePlayers().first()

        assertEquals(3, activePlayers.size)
        assertEquals(true, activePlayers.all { it.isActive })
    }

    @Test
    @Throws(Exception::class)
    fun updatePlayerStats() = runBlocking {
        val player = Player(
            id = 0,
            name = "Jugador Estadísticas",
            gamesPlayed = 0,
            gamesWon = 0,
            highestScore = 0,
            totalScore = 0
        )
        val playerId = playerRepository.createPlayer(player.name, player.avatarResourceId)

        playerDao.incrementGamesPlayed(listOf(playerId))
        playerDao.incrementGamesWon(playerId)
        playerDao.updateHighestScore(playerId, 5000)
        playerDao.updateTotalScore(playerId, 5000)

        val updatedPlayer = playerRepository.getPlayerById(playerId)

        assertNotNull(updatedPlayer)
        assertEquals(1, updatedPlayer?.gamesPlayed)
        assertEquals(1, updatedPlayer?.gamesWon)
        assertEquals(5000, updatedPlayer?.highestScore)
        assertEquals(5000L, updatedPlayer?.totalScore)
    }
}
