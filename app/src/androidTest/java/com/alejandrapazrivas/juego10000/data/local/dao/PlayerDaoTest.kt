package com.alejandrapazrivas.juego10000.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandrapazrivas.juego10000.data.local.AppDatabase
import com.alejandrapazrivas.juego10000.data.local.entity.PlayerEntity
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
 * Pruebas instrumentadas para el DAO de jugadores.
 */
@RunWith(AndroidJUnit4::class)
class PlayerDaoTest {
    private lateinit var playerDao: PlayerDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        playerDao = db.playerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPlayer() = runBlocking {
        val player = PlayerEntity(
            name = "Jugador Test",
            avatarResourceId = 123,
            gamesPlayed = 0,
            gamesWon = 0,
            highestScore = 0,
            totalScore = 0,
            isActive = true
        )

        val playerId = playerDao.insertPlayer(player)

        val retrievedPlayer = playerDao.getPlayerById(playerId)

        assertNotNull(retrievedPlayer)
        assertEquals(player.name, retrievedPlayer?.name)
        assertEquals(player.avatarResourceId, retrievedPlayer?.avatarResourceId)
        assertEquals(player.isActive, retrievedPlayer?.isActive)
    }

    @Test
    @Throws(Exception::class)
    fun updatePlayer() = runBlocking {
        val player = PlayerEntity(
            name = "Jugador Original",
            avatarResourceId = 123,
            gamesPlayed = 0,
            gamesWon = 0,
            highestScore = 0,
            totalScore = 0,
            isActive = true
        )
        val playerId = playerDao.insertPlayer(player)

        val originalPlayer = playerDao.getPlayerById(playerId)
        assertNotNull(originalPlayer)

        val updatedPlayer = originalPlayer!!.copy(
            name = "Jugador Actualizado",
            gamesPlayed = 5,
            gamesWon = 2,
            highestScore = 8000,
            totalScore = 25000
        )
        playerDao.updatePlayer(updatedPlayer)

        val retrievedPlayer = playerDao.getPlayerById(playerId)

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
        val player = PlayerEntity(
            name = "Jugador a Eliminar",
            avatarResourceId = 123,
            isActive = true
        )
        val playerId = playerDao.insertPlayer(player)

        val insertedPlayer = playerDao.getPlayerById(playerId)
        assertNotNull(insertedPlayer)

        playerDao.deletePlayer(insertedPlayer!!)

        val deletedPlayer = playerDao.getPlayerById(playerId)
        assertNull(deletedPlayer)
    }

    @Test
    @Throws(Exception::class)
    fun getAllActivePlayers() = runBlocking {
        val activePlayer1 = PlayerEntity(name = "Activo 1", isActive = true)
        val activePlayer2 = PlayerEntity(name = "Activo 2", isActive = true)
        val inactivePlayer = PlayerEntity(name = "Inactivo", isActive = false)

        playerDao.insertPlayer(activePlayer1)
        playerDao.insertPlayer(activePlayer2)
        playerDao.insertPlayer(inactivePlayer)

        val activePlayers = playerDao.getAllActivePlayers().first()

        assertEquals(2, activePlayers.size)
        assertEquals(true, activePlayers.all { it.isActive })
    }

    @Test
    @Throws(Exception::class)
    fun incrementGamesPlayedAndWon() = runBlocking {
        val player = PlayerEntity(
            name = "Jugador Estadísticas",
            gamesPlayed = 0,
            gamesWon = 0
        )
        val playerId = playerDao.insertPlayer(player)

        playerDao.incrementGamesPlayed(listOf(playerId))

        var updatedPlayer = playerDao.getPlayerById(playerId)
        assertEquals(1, updatedPlayer?.gamesPlayed)
        assertEquals(0, updatedPlayer?.gamesWon)

        playerDao.incrementGamesWon(playerId)

        updatedPlayer = playerDao.getPlayerById(playerId)
        assertEquals(1, updatedPlayer?.gamesPlayed)
        assertEquals(1, updatedPlayer?.gamesWon)
    }

    @Test
    @Throws(Exception::class)
    fun updateHighestScore() = runBlocking {
        val player = PlayerEntity(
            name = "Jugador Puntuación",
            highestScore = 5000
        )
        val playerId = playerDao.insertPlayer(player)

        playerDao.updateHighestScore(playerId, 3000)
        var updatedPlayer = playerDao.getPlayerById(playerId)
        assertEquals(5000, updatedPlayer?.highestScore)

        playerDao.updateHighestScore(playerId, 7000)
        updatedPlayer = playerDao.getPlayerById(playerId)
        assertEquals(7000, updatedPlayer?.highestScore)
    }

    @Test
    @Throws(Exception::class)
    fun updateTotalScore() = runBlocking {
        val player = PlayerEntity(
            name = "Jugador Puntuación Total",
            totalScore = 10000
        )
        val playerId = playerDao.insertPlayer(player)

        playerDao.updateTotalScore(playerId, 5000)
        val updatedPlayer = playerDao.getPlayerById(playerId)
        assertEquals(15000L, updatedPlayer?.totalScore)
    }
}
