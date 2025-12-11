package com.alejandrapazrivas.juego10000.ui.gamewinner

import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ads.AdManager
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

private val TrophyColor = Color(0xFFFFC107)

/**
 * Componente para mostrar el trofeo animado con efecto de brillo
 */
@Composable
private fun AnimatedTrophy() {
    val dimensions = LocalDimensions.current
    val trophySize = dimensions.headerIconSize * 1.5f

    // Animación para el trofeo
    val infiniteTransition = rememberInfiniteTransition(label = "trophy_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    // Animación para el brillo
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_animation"
    )

    Box(
        modifier = Modifier
            .size(trophySize * scale)
            .padding(dimensions.spaceSmall),
        contentAlignment = Alignment.Center
    ) {
        // Efecto de brillo
        Box(
            modifier = Modifier
                .size(trophySize * scale * 1.2f)
                .background(
                    TrophyColor.copy(alpha = shimmerAlpha),
                    RoundedCornerShape(percent = 50)
                )
        )

        // Icono del trofeo
        Icon(
            painter = painterResource(id = R.drawable.ic_trophy),
            contentDescription = "Trofeo",
            modifier = Modifier.size(trophySize * scale),
            tint = TrophyColor
        )
    }
}

@Composable
fun GameVictoryScreen(
    winner: Player?,
    score: Int,
    onBackToHome: () -> Unit,
    adManager: AdManager? = null
) {
    val dimensions = LocalDimensions.current
    val context = LocalContext.current

    // Precargar el interstitial cuando se muestra la pantalla de victoria
    LaunchedEffect(Unit) {
        adManager?.loadInterstitial()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(dimensions.spaceMedium),
            shape = RoundedCornerShape(dimensions.spaceLarge),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimensions.cardElevation
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡VICTORIA!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontSize = dimensions.scoreLarge
                )

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                AnimatedTrophy()

                Spacer(modifier = Modifier.height(dimensions.spaceLarge))

                winner?.let {
                    Text(
                        text = "¡${it.name} ha ganado!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    Text(
                        text = "Puntuación: $score puntos",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

                Button(
                    onClick = {
                        // Mostrar interstitial si está disponible, luego navegar
                        if (adManager != null && adManager.isInterstitialReady()) {
                            (context as? Activity)?.let { activity ->
                                adManager.showInterstitial(activity) {
                                    onBackToHome()
                                }
                            } ?: onBackToHome()
                        } else {
                            onBackToHome()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensions.buttonHeight),
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Volver al menú principal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
