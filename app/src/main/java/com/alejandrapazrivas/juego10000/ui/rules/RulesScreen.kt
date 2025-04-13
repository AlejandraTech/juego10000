package com.alejandrapazrivas.juego10000.ui.rules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.rules.components.RuleSection
import com.alejandrapazrivas.juego10000.ui.rules.components.RulesHeader
import com.alejandrapazrivas.juego10000.ui.rules.components.ScoringCard
import kotlinx.coroutines.delay

/**
 * Pantalla que muestra las reglas del juego 10000.
 * Incluye secciones animadas con información sobre el objetivo, mecánica, puntuación y estrategias.
 * 
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(navController: NavController) {
    // Estados para controlar la animación de aparición de los elementos
    var showHeader by remember { mutableStateOf(false) }
    var showObjective by remember { mutableStateOf(false) }
    var showEntrance by remember { mutableStateOf(false) }
    var showMechanics by remember { mutableStateOf(false) }
    var showScoring by remember { mutableStateOf(false) }
    var showStrategies by remember { mutableStateOf(false) }

    // Efecto que controla la aparición secuencial de los elementos
    LaunchedEffect(key1 = true) {
        showHeader = true
        delay(150)
        showObjective = true
        delay(150)
        showEntrance = true
        delay(150)
        showMechanics = true
        delay(150)
        showScoring = true
        delay(150)
        showStrategies = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reglas del Juego",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(elevation = 4.dp)
            )
        }
    ) { paddingValues ->
        // Fondo con gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Contenido principal con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Encabezado animado
                AnimatedVisibility(
                    visible = showHeader,
                    enter = fadeIn(animationSpec = tween(500)) +
                            expandVertically(animationSpec = tween(500))
                ) {
                    RulesHeader()
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Objetivo del juego
                AnimatedVisibility(
                    visible = showObjective,
                    enter = fadeIn(animationSpec = tween(500)) +
                            expandVertically(animationSpec = tween(500))
                ) {
                    RuleSection(
                        icon = R.drawable.ic_trophy,
                        title = "Objetivo del Juego",
                        content = "Ser el primer jugador en alcanzar 10,000 puntos para ganar la partida."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Entrada en juego
                AnimatedVisibility(
                    visible = showEntrance,
                    enter = fadeIn(animationSpec = tween(500)) +
                            expandVertically(animationSpec = tween(500))
                ) {
                    RuleSection(
                        icon = R.drawable.ic_dice,
                        title = "Entrada en Juego",
                        content = "Para comenzar a acumular puntos, cada jugador debe obtener al menos 500 puntos en su primer turno. Hasta que no se consiga esta puntuación, no se registrarán puntos en el marcador."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mecánica de turnos
                AnimatedVisibility(
                    visible = showMechanics,
                    enter = fadeIn(animationSpec = tween(500)) +
                            expandVertically(animationSpec = tween(500))
                ) {
                    RuleSection(
                        icon = R.drawable.ic_dice,
                        title = "Mecánica de Turnos",
                        content = "1. Cada jugador lanza 6 dados en su turno.\n" +
                                "2. Después de cada lanzamiento, debe apartar al menos un dado con puntuación.\n" +
                                "3. Puede elegir relanzar los dados restantes para acumular más puntos.\n" +
                                "4. Si en algún lanzamiento no obtiene ningún dado con puntuación, pierde todos los puntos acumulados en ese turno y pasa al siguiente jugador.\n" +
                                "5. El jugador puede decidir 'plantarse' en cualquier momento y sumar los puntos acumulados a su total.\n" +
                                "6. Si utiliza los 6 dados con puntuación, obtiene un nuevo lanzamiento con todos los dados."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sistema de puntuación
                AnimatedVisibility(
                    visible = showScoring,
                    enter = fadeIn(animationSpec = tween(500)) +
                            expandVertically(animationSpec = tween(500))
                ) {
                    ScoringCard()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estrategias
                AnimatedVisibility(
                    visible = showStrategies,
                    enter = fadeIn(animationSpec = tween(500)) +
                            expandVertically(animationSpec = tween(500))
                ) {
                    RuleSection(
                        icon = R.drawable.ic_settings,
                        title = "Estrategias",
                        content = "• Equilibra el riesgo: Decide cuándo plantarte y cuándo seguir lanzando.\n" +
                                "• Prioriza dados de alto valor: Los 1 y 5 son valiosos individualmente.\n" +
                                "• Busca combinaciones: Tres o más dados iguales otorgan puntuaciones altas.\n" +
                                "• Observa a tus oponentes: Ajusta tu estrategia según sus puntuaciones."
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
