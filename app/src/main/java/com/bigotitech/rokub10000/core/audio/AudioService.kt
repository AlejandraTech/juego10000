package com.bigotitech.rokub10000.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio centralizado para la reproducción de efectos de sonido del juego.
 * Gestiona los sonidos de lanzamiento de dados y victoria, respetando las preferencias del usuario.
 */
@Singleton
class AudioService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesManager: UserPreferencesManager
) {
    private var diceRollSound: MediaPlayer? = null
    private var winSound: MediaPlayer? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val TAG = "AudioService"
    }

    init {
        initializeSounds()
    }

    /**
     * Inicializa los MediaPlayers para los efectos de sonido
     */
    private fun initializeSounds() {
        try {
            diceRollSound?.release()
            winSound?.release()

            diceRollSound = MediaPlayer.create(context, R.raw.dice_roll)?.apply {
                setOnCompletionListener { seekTo(0) }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Error en diceRollSound: what=$what, extra=$extra")
                    false
                }
            }

            winSound = MediaPlayer.create(context, R.raw.win_sound)?.apply {
                setOnCompletionListener { seekTo(0) }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Error en winSound: what=$what, extra=$extra")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar sonidos: ${e.message}")
        }
    }

    /**
     * Reproduce el sonido de lanzamiento de dados si el sonido está habilitado
     */
    fun playDiceRollSound() {
        scope.launch {
            try {
                val soundEnabled = userPreferencesManager.soundEnabled.first()
                if (!soundEnabled) return@launch

                diceRollSound?.let { player ->
                    if (player.isPlaying) {
                        player.stop()
                        player.prepare()
                    }
                    player.start()
                } ?: run {
                    initializeSounds()
                    diceRollSound?.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al reproducir sonido de dados: ${e.message}")
                // Reintentar inicializando de nuevo
                initializeSounds()
            }
        }
    }

    /**
     * Reproduce el sonido de lanzamiento de dados de forma síncrona (para uso interno)
     * @param checkPreferences Si true, verifica las preferencias antes de reproducir
     */
    suspend fun playDiceRollSoundSync(checkPreferences: Boolean = true) {
        try {
            if (checkPreferences) {
                val soundEnabled = userPreferencesManager.soundEnabled.first()
                if (!soundEnabled) return
            }

            diceRollSound?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                    player.prepare()
                }
                player.start()
            } ?: run {
                initializeSounds()
                diceRollSound?.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al reproducir sonido de dados: ${e.message}")
        }
    }

    /**
     * Reproduce el sonido de victoria si el sonido está habilitado
     */
    fun playWinSound() {
        scope.launch {
            try {
                val soundEnabled = userPreferencesManager.soundEnabled.first()
                if (!soundEnabled) return@launch

                winSound?.let { player ->
                    if (player.isPlaying) {
                        player.stop()
                        player.prepare()
                    }
                    player.start()
                } ?: run {
                    initializeSounds()
                    winSound?.start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al reproducir sonido de victoria: ${e.message}")
                initializeSounds()
            }
        }
    }

    /**
     * Reproduce el sonido de victoria de forma síncrona (para uso interno)
     * @param checkPreferences Si true, verifica las preferencias antes de reproducir
     */
    suspend fun playWinSoundSync(checkPreferences: Boolean = true) {
        try {
            if (checkPreferences) {
                val soundEnabled = userPreferencesManager.soundEnabled.first()
                if (!soundEnabled) return
            }

            winSound?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                    player.prepare()
                }
                player.start()
            } ?: run {
                initializeSounds()
                winSound?.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al reproducir sonido de victoria: ${e.message}")
        }
    }

    /**
     * Verifica si el sonido está habilitado en las preferencias
     */
    suspend fun isSoundEnabled(): Boolean {
        return userPreferencesManager.soundEnabled.first()
    }

    /**
     * Libera los recursos de los MediaPlayers
     * Debe llamarse cuando el servicio ya no se necesita
     */
    fun release() {
        try {
            diceRollSound?.release()
            diceRollSound = null
            winSound?.release()
            winSound = null
        } catch (e: Exception) {
            Log.e(TAG, "Error al liberar recursos de audio: ${e.message}")
        }
    }

    /**
     * Reinicializa los sonidos (útil después de un error)
     */
    fun reinitialize() {
        release()
        initializeSounds()
    }
}
