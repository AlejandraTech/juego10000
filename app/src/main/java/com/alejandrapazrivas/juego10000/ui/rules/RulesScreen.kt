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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.rules.components.RuleSection
import com.alejandrapazrivas.juego10000.ui.rules.components.RuleSectionBulleted
import com.alejandrapazrivas.juego10000.ui.rules.components.RuleSectionNumbered
import com.alejandrapazrivas.juego10000.ui.rules.components.RulesHeader
import com.alejandrapazrivas.juego10000.ui.rules.components.ScoringCard
import com.alejandrapazrivas.juego10000.ads.AdConstants
import com.alejandrapazrivas.juego10000.ui.common.components.ads.BannerAd
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay

/**
 * Pantalla que muestra las reglas del juego 10000.
 * Diseño moderno sin tarjetas rectangulares, con fondo estático.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(navController: NavController) {
    val dimensions = LocalDimensions.current

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
        delay(100)
        showObjective = true
        delay(100)
        showEntrance = true
        delay(100)
        showMechanics = true
        delay(100)
        showScoring = true
        delay(100)
        showStrategies = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.rules_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        // Fondo estático con gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            // Contenido principal con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensions.spaceMedium)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                // Encabezado animado
                AnimatedVisibility(
                    visible = showHeader,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400))
                ) {
                    RulesHeader()
                }

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                // Objetivo del juego
                AnimatedVisibility(
                    visible = showObjective,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400))
                ) {
                    RuleSection(
                        icon = R.drawable.ic_trophy,
                        title = stringResource(R.string.game_objective),
                        content = stringResource(R.string.objective_content)
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                // Entrada en juego
                AnimatedVisibility(
                    visible = showEntrance,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400))
                ) {
                    RuleSection(
                        icon = R.drawable.ic_dice,
                        title = stringResource(R.string.game_entry),
                        content = stringResource(R.string.entry_content)
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                // Mecánica de turnos
                AnimatedVisibility(
                    visible = showMechanics,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400))
                ) {
                    RuleSectionNumbered(
                        icon = R.drawable.ic_dice,
                        title = stringResource(R.string.turn_mechanics),
                        items = listOf(
                            stringResource(R.string.turn_mechanic_1),
                            stringResource(R.string.turn_mechanic_2),
                            stringResource(R.string.turn_mechanic_3),
                            stringResource(R.string.turn_mechanic_4),
                            stringResource(R.string.turn_mechanic_5),
                            stringResource(R.string.turn_mechanic_6)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                // Sistema de puntuación
                AnimatedVisibility(
                    visible = showScoring,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400))
                ) {
                    ScoringCard()
                }

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                // Estrategias
                AnimatedVisibility(
                    visible = showStrategies,
                    enter = fadeIn(animationSpec = tween(400)) +
                            expandVertically(animationSpec = tween(400))
                ) {
                    RuleSectionBulleted(
                        icon = R.drawable.ic_settings,
                        title = stringResource(R.string.strategies),
                        items = listOf(
                            stringResource(R.string.strategy_1),
                            stringResource(R.string.strategy_2),
                            stringResource(R.string.strategy_3),
                            stringResource(R.string.strategy_4)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                BannerAd(
                    adUnitId = AdConstants.BANNER_RULES,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))
            }
        }
    }
}
