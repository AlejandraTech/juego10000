package com.alejandrapazrivas.juego10000.ui.common.components.toast

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
    // Constantes de tiempo para la gestión de mensajes
    const val ANIMATION_DELAY_MS = 100L
    const val AUTO_HIDE_DELAY_MS = 3000L
    
    // Categorías de mensajes esenciales que siempre se mostrarán
    private object MessageCategories {
        val POINTS = listOf("puntos", "guardado")
        val GAME_EVENTS = listOf("perdido el turno", "superado los 10,000", "ganado")
        val ERRORS = listOf("error", "Error")
        
        // Todos los mensajes esenciales combinados
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
            // Mensajes de éxito
            MessageCategories.POINTS.any { message.contains(it, ignoreCase = true) } ||
            message.contains("ganado", ignoreCase = true) -> ToastType.SUCCESS
            
            // Mensajes de error
            MessageCategories.ERRORS.any { message.contains(it, ignoreCase = true) } ||
            message.contains("perdido", ignoreCase = true) || 
            message.contains("superado", ignoreCase = true) -> ToastType.ERROR
            
            // Mensajes informativos (por defecto)
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
    // Estados para controlar la visualización del toast
    var showToast by remember { mutableStateOf(false) }
    var currentMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.INFO) }
    
    // Procesar nuevo mensaje
    LaunchedEffect(message) {
        if (message != null && GameMessageManager.isEssentialMessage(message)) {
            // Ocultar cualquier mensaje anterior
            showToast = false
            currentMessage = null
            
            // Pequeña pausa para asegurar que la animación de cierre se complete
            delay(GameMessageManager.ANIMATION_DELAY_MS)
            
            // Mostrar el nuevo mensaje
            currentMessage = message
            toastType = GameMessageManager.getToastType(message)
            showToast = true
            
            // Auto-ocultar el mensaje después de un tiempo
            delay(GameMessageManager.AUTO_HIDE_DELAY_MS)
            
            // Solo ocultar automáticamente si el mensaje aún se muestra
            if (showToast) {
                showToast = false
                onMessageShown()
            }
        } else if (message != null) {
            onMessageShown()
        }
    }
    
    // Función para cerrar el mensaje manualmente
    val closeMessage = {
        showToast = false
        onMessageShown()
    }
    
    // Mostrar el toast si hay un mensaje
    currentMessage?.let {
        GameToast(
            message = it,
            isVisible = showToast,
            type = toastType,
            onClose = closeMessage,
            modifier = modifier
        )
    }
}
