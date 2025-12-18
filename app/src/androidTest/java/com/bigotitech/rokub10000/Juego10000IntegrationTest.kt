package com.bigotitech.rokub10000

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bigotitech.rokub10000.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de integración para la aplicación Juego10000.
 * 
 * Estas pruebas verifican el flujo completo de la aplicación, desde la pantalla de inicio
 * hasta las diferentes funcionalidades principales.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class Juego10000IntegrationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun appLaunchesSuccessfully() {
        // Verificar que la aplicación se inicia correctamente y muestra la pantalla de inicio
        composeTestRule.onNodeWithText("Juego 10000", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Jugar", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Jugadores", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Reglas", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Estadísticas", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun navigateThroughMainScreens() {
        // Navegar a la pantalla de jugadores
        composeTestRule.onNodeWithText("Jugadores", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Jugadores", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
        
        // Navegar a la pantalla de reglas
        composeTestRule.onNodeWithText("Reglas", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Reglas del Juego", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
        
        // Navegar a la pantalla de estadísticas
        composeTestRule.onNodeWithText("Estadísticas", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Estadísticas", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
        
        // Navegar a la pantalla de configuración
        composeTestRule.onNodeWithContentDescription("Configuración", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Configuración", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
        
        // Verificar que estamos de vuelta en la pantalla de inicio
        composeTestRule.onNodeWithText("Juego 10000", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun openGameModeDialog() {
        // Hacer clic en el botón de jugar
        composeTestRule.onNodeWithText("Jugar", ignoreCase = true).performClick()
        
        // Verificar que se muestra el diálogo de selección de modo de juego
        composeTestRule.onNodeWithText("Selecciona el modo de juego", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Multijugador", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Un jugador", ignoreCase = true).assertIsDisplayed()
        
        // Cerrar el diálogo
        composeTestRule.onNodeWithText("Cancelar", ignoreCase = true).performClick()
        
        // Verificar que volvemos a la pantalla de inicio
        composeTestRule.onNodeWithText("Juego 10000", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun checkRulesContent() {
        // Navegar a la pantalla de reglas
        composeTestRule.onNodeWithText("Reglas", ignoreCase = true).performClick()
        
        // Verificar que se muestra el contenido de las reglas
        composeTestRule.onNodeWithText("Reglas del Juego", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Objetivo", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Puntuación", ignoreCase = true).assertIsDisplayed()
        
        // Verificar que se muestran las combinaciones de puntuación
        composeTestRule.onNodeWithText("Combinaciones", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Tres unos", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("1000", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
    }

    @Test
    fun checkSettingsScreen() {
        // Navegar a la pantalla de configuración
        composeTestRule.onNodeWithContentDescription("Configuración", ignoreCase = true).performClick()
        
        // Verificar que se muestran las opciones de configuración
        composeTestRule.onNodeWithText("Configuración", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Sonido", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Vibración", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Dificultad del Bot", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Puntuación objetivo", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
    }

    @Test
    fun checkPlayersScreen() {
        // Navegar a la pantalla de jugadores
        composeTestRule.onNodeWithText("Jugadores", ignoreCase = true).performClick()
        
        // Verificar que se muestra la pantalla de jugadores
        composeTestRule.onNodeWithText("Jugadores", ignoreCase = true).assertIsDisplayed()
        
        // Verificar que se muestra el botón para añadir jugadores
        composeTestRule.onNodeWithContentDescription("Añadir jugador", ignoreCase = true).assertIsDisplayed()
        
        // Volver a la pantalla de inicio
        composeTestRule.onNodeWithContentDescription("Volver", ignoreCase = true).performClick()
    }
}
