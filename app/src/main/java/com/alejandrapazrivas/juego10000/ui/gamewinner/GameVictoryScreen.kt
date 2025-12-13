package com.alejandrapazrivas.juego10000.ui.gamewinner

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ads.AdManager
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.gamewinner.components.AnimatedTrophySection
import com.alejandrapazrivas.juego10000.ui.gamewinner.components.AnimatedVictoryBackground
import com.alejandrapazrivas.juego10000.ui.gamewinner.components.ConfettiAnimation
import com.alejandrapazrivas.juego10000.ui.gamewinner.components.VictoryTitle
import com.alejandrapazrivas.juego10000.ui.gamewinner.components.WinnerCard
import kotlinx.coroutines.delay

/**
 * Pantalla de victoria que muestra la celebración cuando un jugador gana.
 * Incluye animaciones de confeti, trofeo y la información del ganador.
 *
 * @param winner Jugador ganador
 * @param score Puntuación final
 * @param onBackToHome Callback para volver al menú principal
 * @param adManager Gestor de anuncios opcional
 */
@Composable
fun GameVictoryScreen(
    winner: Player?,
    score: Int,
    onBackToHome: () -> Unit,
    adManager: AdManager? = null
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current

    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.8f) }

    var displayedScore by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        adManager?.loadInterstitial()

        contentAlpha.animateTo(1f, animationSpec = tween(500))
        contentScale.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))

        val steps = 30
        val increment = score / steps
        repeat(steps) {
            delay(30)
            displayedScore = minOf(displayedScore + increment, score)
        }
        displayedScore = score
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVictoryBackground()

        ConfettiAnimation()

        VictoryContent(
            winner = winner,
            displayedScore = displayedScore,
            contentAlpha = contentAlpha.value,
            contentScale = contentScale.value,
            onBackToHome = {
                if (adManager != null && adManager.isInterstitialReady()) {
                    (context as? Activity)?.let { activity ->
                        adManager.showInterstitial(activity) {
                            onBackToHome()
                        }
                    } ?: onBackToHome()
                } else {
                    onBackToHome()
                }
            }
        )
    }
}

@Composable
private fun VictoryContent(
    winner: Player?,
    displayedScore: Int,
    contentAlpha: Float,
    contentScale: Float,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensions.spaceLarge)
            .alpha(contentAlpha)
            .scale(contentScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VictoryTitle()

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        AnimatedTrophySection()

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        winner?.let {
            WinnerCard(
                playerName = it.name,
                score = displayedScore
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        BackToHomeButton(onClick = onBackToHome)
    }
}

@Composable
private fun BackToHomeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(dimensions.buttonHeight)
            .shadow(
                elevation = 8.dp,
                shape = ButtonShape,
                spotColor = Primary.copy(alpha = 0.5f)
            ),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(dimensions.spaceSmall))
        Text(
            text = stringResource(R.string.back_to_main_menu),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
