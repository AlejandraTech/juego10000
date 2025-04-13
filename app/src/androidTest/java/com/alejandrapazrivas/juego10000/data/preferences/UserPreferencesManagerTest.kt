package com.alejandrapazrivas.juego10000.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandrapazrivas.juego10000.domain.model.BotDifficulty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Pruebas instrumentadas para el gestor de preferencias del usuario.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UserPreferencesManagerTest {
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = testCoroutineScope,
        produceFile = { testContext.preferencesDataStoreFile("test_user_preferences") }
    )

    private lateinit var userPreferencesManager: UserPreferencesManager

    @Before
    fun setup() {
        userPreferencesManager = UserPreferencesManager(testContext)
    }

    @After
    fun cleanup() {
        File(testContext.filesDir, "datastore/test_user_preferences.preferences_pb").delete()
    }

    @Test
    fun testDefaultPreferences() = testCoroutineScope.runTest {
        userPreferencesManager.setSoundEnabled(true)
        userPreferencesManager.setVibrationEnabled(true)
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)

        assertEquals(true, userPreferencesManager.soundEnabled.first())
        assertEquals(true, userPreferencesManager.vibrationEnabled.first())
        assertEquals(BotDifficulty.INTERMEDIATE.name, userPreferencesManager.botDifficulty.first())
    }

    @Test
    fun testUpdateSoundEnabled() = testCoroutineScope.runTest {
        userPreferencesManager.setSoundEnabled(true)
        assertTrue(userPreferencesManager.soundEnabled.first())

        userPreferencesManager.setSoundEnabled(false)

        assertFalse(userPreferencesManager.soundEnabled.first())
    }

    @Test
    fun testUpdateVibrationEnabled() = testCoroutineScope.runTest {
        userPreferencesManager.setVibrationEnabled(true)
        assertTrue(userPreferencesManager.vibrationEnabled.first())

        userPreferencesManager.setVibrationEnabled(false)

        assertFalse(userPreferencesManager.vibrationEnabled.first())
    }

    @Test
    fun testUpdateBotDifficulty() = testCoroutineScope.runTest {
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)
        assertEquals(BotDifficulty.INTERMEDIATE.name, userPreferencesManager.botDifficulty.first())

        userPreferencesManager.setBotDifficulty(BotDifficulty.EXPERT.name)

        assertEquals(BotDifficulty.EXPERT.name, userPreferencesManager.botDifficulty.first())
    }

    @Test
    fun testResetToDefaults() = testCoroutineScope.runTest {
        userPreferencesManager.setSoundEnabled(true)
        userPreferencesManager.setVibrationEnabled(true)
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)

        userPreferencesManager.setSoundEnabled(false)
        userPreferencesManager.setVibrationEnabled(false)
        userPreferencesManager.setBotDifficulty(BotDifficulty.BEGINNER.name)

        assertFalse(userPreferencesManager.soundEnabled.first())
        assertFalse(userPreferencesManager.vibrationEnabled.first())
        assertEquals(BotDifficulty.BEGINNER.name, userPreferencesManager.botDifficulty.first())

        userPreferencesManager.setSoundEnabled(true)
        userPreferencesManager.setVibrationEnabled(true)
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)

        assertTrue(userPreferencesManager.soundEnabled.first())
        assertTrue(userPreferencesManager.vibrationEnabled.first())
        assertEquals(BotDifficulty.INTERMEDIATE.name, userPreferencesManager.botDifficulty.first())
    }

    @Test
    fun testPersistenceAcrossInstances() = testCoroutineScope.runTest {
        userPreferencesManager.setSoundEnabled(true)
        userPreferencesManager.setVibrationEnabled(true)
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)

        userPreferencesManager.setSoundEnabled(false)
        userPreferencesManager.setVibrationEnabled(false)
        userPreferencesManager.setBotDifficulty(BotDifficulty.EXPERT.name)

        val newPreferencesManager = UserPreferencesManager(testContext)

        assertFalse(newPreferencesManager.soundEnabled.first())
        assertFalse(newPreferencesManager.vibrationEnabled.first())
        assertEquals(BotDifficulty.EXPERT.name, newPreferencesManager.botDifficulty.first())
    }

    @Test
    fun testClearPreferences() = testCoroutineScope.runTest {
        userPreferencesManager.setSoundEnabled(true)
        userPreferencesManager.setVibrationEnabled(true)
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)

        userPreferencesManager.setSoundEnabled(false)
        userPreferencesManager.setVibrationEnabled(false)
        userPreferencesManager.setBotDifficulty(BotDifficulty.BEGINNER.name)

        assertFalse(userPreferencesManager.soundEnabled.first())
        assertFalse(userPreferencesManager.vibrationEnabled.first())
        assertEquals(BotDifficulty.BEGINNER.name, userPreferencesManager.botDifficulty.first())

        userPreferencesManager.setSoundEnabled(true)
        userPreferencesManager.setVibrationEnabled(true)
        userPreferencesManager.setBotDifficulty(BotDifficulty.INTERMEDIATE.name)

        assertTrue(userPreferencesManager.soundEnabled.first())
        assertTrue(userPreferencesManager.vibrationEnabled.first())
        assertEquals(BotDifficulty.INTERMEDIATE.name, userPreferencesManager.botDifficulty.first())
    }
}
