package com.bigotitech.rokub10000.ui.common.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * Clase que gestiona los mensajes del juego, filtrando los esenciales
 * y controlando su visualización
 */
object GameMessageManager {
    const val ANIMATION_DELAY_MS = 100L
    const val AUTO_HIDE_DELAY_MS = 3000L

    private object MessageCategories {
        val POINTS = listOf("puntos", "guardado")
        val GAME_EVENTS = listOf(
            "perdido el turno",
            "pierdes el turno",
            "sin puntuación",
            "superado los 10,000",
            "ganado"
        )
        val ERRORS = listOf("error", "Error")
        val ALL = POINTS + GAME_EVENTS + ERRORS
    }
    
    /**
     * Determina si un mensaje es esencial basado en su contenido
     * 
     * @param message El mensaje a evaluar
     * @return true si el mensaje es esencial, false en caso contrario
     */
    fun isEssentialMessage(message: String): Boolean {
        return MessageCategories.ALL.any { message.contains(it, ignoreCase = true) }
    }
    
    /**
     * Determina el tipo de toast basado en el contenido del mensaje
     * 
     * @param message El mensaje a evaluar
     * @return El tipo de toast correspondiente (SUCCESS, ERROR, INFO)
     */
    fun getToastType(message: String): ToastType {
        return when {
            MessageCategories.POINTS.any { message.contains(it, ignoreCase = true) } ||
            message.contains("ganado", ignoreCase = true) -> ToastType.SUCCESS

            MessageCategories.ERRORS.any { message.contains(it, ignoreCase = true) } ||
            message.contains("perdido", ignoreCase = true) ||
            message.contains("pierdes", ignoreCase = true) ||
            message.contains("sin puntuación", ignoreCase = true) ||
            message.contains("superado", ignoreCase = true) -> ToastType.ERROR

            else -> ToastType.INFO
        }
    }
}

/**
 * Componente que gestiona la visualización de mensajes esenciales
 * 
 * @param message El mensaje a mostrar (puede ser null)
 * @param onMessageShown Callback para notificar que el mensaje ha sido mostrado
 * @param modifier Modificador opcional para personalizar el toast
 */
@Composable
fun GameMessageHandler(
    message: String?,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showToast by remember { mutableStateOf(false) }
    var currentMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    LaunchedEffect(message) {
        if (message != null && GameMessageManager.isEssentialMessage(message)) {
            showToast = false
            currentMessage = null
            delay(GameMessageManager.ANIMATION_DELAY_MS)

            currentMessage = message
            toastType = GameMessageManager.getToastType(message)
            showToast = true

            delay(GameMessageManager.AUTO_HIDE_DELAY_MS)

            if (showToast) {
                showToast = false
                currentMessage = null
                onMessageShown()
            }
        } else if (message != null) {
            onMessageShown()
        } else {
            showToast = false
            currentMessage = null
        }
    }

    val closeMessage = {
        showToast = false
        currentMessage = null
        onMessageShown()
    }

    val shouldShowToast = showToast &&
            currentMessage != null &&
            message != null &&
            message == currentMessage &&
            GameMessageManager.isEssentialMessage(message)

    if (shouldShowToast) {
        GameToast(
            message = currentMessage!!,
            isVisible = true,
            type = toastType,
            onClose = closeMessage,
            modifier = modifier
        )
    }
}
