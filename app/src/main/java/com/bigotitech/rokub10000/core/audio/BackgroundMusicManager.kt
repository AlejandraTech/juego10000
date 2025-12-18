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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de música de fondo para la aplicación.
 * Reproduce música en bucle respetando las preferencias del usuario.
 */
@Singleton
class BackgroundMusicManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesManager: UserPreferencesManager
) {
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var isMusicEnabled = true
    private var currentVolume = 0.5f
    private var shouldPlayMusic = false
    private var isPaused = false
    private var isInGameMode = false

    init {
        observePreferences()
    }

    /**
     * Observa los cambios en las preferencias de música
     */
    private fun observePreferences() {
        scope.launch {
            combine(
                userPreferencesManager.musicEnabled,
                userPreferencesManager.musicVolume
            ) { enabled, volume ->
                Pair(enabled, volume)
            }.collect { (enabled, volume) ->
                val wasEnabled = isMusicEnabled
                isMusicEnabled = enabled
                currentVolume = volume

                when {
                    // Si se activa la música (cambió de false a true)
                    enabled && !wasEnabled && shouldPlayMusic -> {
                        isPaused = false
                        ensureMusicPlaying()
                        setVolume(volume)
                    }
                    // Si la música está activa y debería sonar
                    enabled && shouldPlayMusic && !isPaused -> {
                        ensureMusicPlaying()
                        setVolume(volume)
                    }
                    // Solo actualizar volumen si está sonando
                    enabled && mediaPlayer?.isPlaying == true -> {
                        setVolume(volume)
                    }
                    // Si se desactiva la música
                    !enabled -> {
                        stopPlayback()
                    }
                }
            }
        }
    }

    /**
     * Detiene la reproducción sin cambiar el estado de shouldPlayMusic
     */
    private fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                try {
                    it.pause()
                } catch (e: Exception) {
                    Log.e(TAG, "Error al detener reproducción: ${e.message}")
                }
            }
        }
    }

    /**
     * Inicia la reproducción de música de fondo
     */
    fun startMusic() {
        shouldPlayMusic = true
        isPaused = false
        if (isMusicEnabled) {
            ensureMusicPlaying()
        }
    }

    /**
     * Detiene la reproducción de música de fondo
     */
    fun stopMusic() {
        shouldPlayMusic = false
        isPaused = false
        releaseMediaPlayer()
    }

    /**
     * Pausa la música temporalmente (para ciclo de vida de la app)
     */
    fun pauseMusic() {
        isPaused = true
        mediaPlayer?.let {
            if (it.isPlaying) {
                try {
                    it.pause()
                } catch (e: Exception) {
                    Log.e(TAG, "Error al pausar música: ${e.message}")
                }
            }
        }
    }

    /**
     * Pausa la música para la pantalla de juego.
     * La música no se reanudará hasta llamar a resumeFromGame().
     */
    fun pauseForGame() {
        isInGameMode = true
        pauseMusic()
    }

    /**
     * Reanuda la música al salir de la pantalla de juego.
     */
    fun resumeFromGame() {
        isInGameMode = false
        resumeMusic()
    }

    /**
     * Reanuda la música si estaba reproduciéndose.
     * No reanuda si estamos en modo juego.
     */
    fun resumeMusic() {
        // No reanudar si estamos en modo juego
        if (isInGameMode) {
            return
        }

        isPaused = false
        if (isMusicEnabled && shouldPlayMusic) {
            mediaPlayer?.let {
                try {
                    it.start()
                } catch (e: Exception) {
                    Log.e(TAG, "Error al reanudar música: ${e.message}")
                    ensureMusicPlaying()
                }
            } ?: ensureMusicPlaying()
        }
    }

    /**
     * Actualiza el volumen de la música
     */
    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.let {
            try {
                it.setVolume(currentVolume, currentVolume)
            } catch (e: Exception) {
                Log.e(TAG, "Error al ajustar volumen: ${e.message}")
            }
        }
    }

    /**
     * Asegura que la música esté reproduciéndose
     */
    private fun ensureMusicPlaying() {
        if (mediaPlayer == null) {
            initializeMediaPlayer()
        }

        mediaPlayer?.let {
            if (!it.isPlaying) {
                try {
                    it.start()
                } catch (e: Exception) {
                    Log.e(TAG, "Error al iniciar música: ${e.message}")
                    // Reintentar inicializando de nuevo
                    releaseMediaPlayer()
                    initializeMediaPlayer()
                    mediaPlayer?.start()
                }
            }
        }
    }

    /**
     * Inicializa el MediaPlayer
     */
    private fun initializeMediaPlayer() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.main_menu).apply {
                isLooping = true
                setVolume(currentVolume, currentVolume)
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Error en MediaPlayer: what=$what, extra=$extra")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar MediaPlayer: ${e.message}")
        }
    }

    /**
     * Libera los recursos del MediaPlayer
     */
    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error al liberar MediaPlayer: ${e.message}")
        }
    }

    /**
     * Libera todos los recursos (llamar al destruir la aplicación)
     */
    fun release() {
        releaseMediaPlayer()
    }

    companion object {
        private const val TAG = "BackgroundMusicManager"
    }
}
