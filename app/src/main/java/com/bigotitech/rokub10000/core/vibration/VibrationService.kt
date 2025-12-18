package com.bigotitech.rokub10000.core.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
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
 * Servicio centralizado para la gestión de feedback háptico (vibración).
 * Proporciona diferentes patrones de vibración para distintos eventos del juego,
 * respetando las preferencias del usuario.
 */
@Singleton
class VibrationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesManager: UserPreferencesManager
) {
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val TAG = "VibrationService"

        // Duraciones de vibración en milisegundos
        private const val DURATION_SHORT = 50L
        private const val DURATION_MEDIUM = 100L
        private const val DURATION_LONG = 200L

        // Patrones de vibración (pausa, vibración, pausa, vibración, ...)
        private val PATTERN_DICE_ROLL = longArrayOf(0, 100)
        private val PATTERN_WIN = longArrayOf(0, 100, 100, 200, 100, 300)
        private val PATTERN_ERROR = longArrayOf(0, 50, 50, 50)
        private val PATTERN_SUCCESS = longArrayOf(0, 50, 50, 100)
    }

    /**
     * Vibración corta para eventos simples (selección de dado, click)
     */
    fun vibrateShort() {
        scope.launch {
            vibrateIfEnabled(DURATION_SHORT)
        }
    }

    /**
     * Vibración media para lanzamiento de dados
     */
    fun vibrateDiceRoll() {
        scope.launch {
            vibratePatternIfEnabled(PATTERN_DICE_ROLL)
        }
    }

    /**
     * Vibración de lanzamiento de dados de forma síncrona
     */
    suspend fun vibrateDiceRollSync() {
        vibratePatternIfEnabled(PATTERN_DICE_ROLL)
    }

    /**
     * Vibración larga para victoria
     */
    fun vibrateWin() {
        scope.launch {
            vibratePatternIfEnabled(PATTERN_WIN)
        }
    }

    /**
     * Vibración de victoria de forma síncrona
     */
    suspend fun vibrateWinSync() {
        vibratePatternIfEnabled(PATTERN_WIN)
    }

    /**
     * Vibración de error (turno perdido, selección inválida)
     */
    fun vibrateError() {
        scope.launch {
            vibratePatternIfEnabled(PATTERN_ERROR)
        }
    }

    /**
     * Vibración de éxito (puntos guardados)
     */
    fun vibrateSuccess() {
        scope.launch {
            vibratePatternIfEnabled(PATTERN_SUCCESS)
        }
    }

    /**
     * Vibración personalizada con duración específica
     * @param durationMs Duración de la vibración en milisegundos
     */
    fun vibrateCustom(durationMs: Long) {
        scope.launch {
            vibrateIfEnabled(durationMs)
        }
    }

    /**
     * Vibración personalizada con patrón específico
     * @param pattern Patrón de vibración (alternancias de pausa y vibración)
     * @param repeat Índice del patrón donde repetir (-1 para no repetir)
     */
    fun vibratePattern(pattern: LongArray, repeat: Int = -1) {
        scope.launch {
            vibratePatternIfEnabled(pattern, repeat)
        }
    }

    /**
     * Ejecuta vibración simple si está habilitada
     */
    private suspend fun vibrateIfEnabled(durationMs: Long) {
        try {
            val vibrationEnabled = userPreferencesManager.vibrationEnabled.first()
            if (!vibrationEnabled) return

            if (!vibrator.hasVibrator()) {
                Log.d(TAG, "El dispositivo no tiene vibrador")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al activar vibración: ${e.message}")
        }
    }

    /**
     * Ejecuta vibración con patrón si está habilitada
     */
    private suspend fun vibratePatternIfEnabled(pattern: LongArray, repeat: Int = -1) {
        try {
            val vibrationEnabled = userPreferencesManager.vibrationEnabled.first()
            if (!vibrationEnabled) return

            if (!vibrator.hasVibrator()) {
                Log.d(TAG, "El dispositivo no tiene vibrador")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, repeat)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al activar vibración con patrón: ${e.message}")
        }
    }

    /**
     * Verifica si la vibración está habilitada en las preferencias
     */
    suspend fun isVibrationEnabled(): Boolean {
        return userPreferencesManager.vibrationEnabled.first()
    }

    /**
     * Verifica si el dispositivo tiene capacidad de vibración
     */
    fun hasVibrator(): Boolean {
        return vibrator.hasVibrator()
    }

    /**
     * Cancela cualquier vibración en curso
     */
    fun cancel() {
        try {
            vibrator.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error al cancelar vibración: ${e.message}")
        }
    }
}
