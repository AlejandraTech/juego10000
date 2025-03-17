package com.alejandrapazrivas.juego10000.ui.gamewinner

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape

private val TrophyColor = Color(0xFFFFC107)
private val TrophySize = 120.dp
private val CardCornerRadius = 24.dp
private val VictoryTitleSize = 32.sp

/**
 * Componente para mostrar el trofeo animado con efecto de brillo
 */
@Composable
private fun AnimatedTrophy() {
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
            .size(TrophySize * scale)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Efecto de brillo
        Box(
            modifier = Modifier
                .size(TrophySize * scale * 1.2f)
                .background(
                    TrophyColor.copy(alpha = shimmerAlpha),
                    RoundedCornerShape(percent = 50)
                )
        )
        
        // Icono del trofeo
        Icon(
            painter = painterResource(id = R.drawable.ic_trophy),
            contentDescription = "Trofeo",
            modifier = Modifier.size(TrophySize * scale),
            tint = TrophyColor
        )
    }
}

@Composable
fun GameVictoryScreen(
    winner: Player?,
    score: Int,
    onBackToHome: () -> Unit
) {

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
                .padding(16.dp),
            shape = RoundedCornerShape(CardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡VICTORIA!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontSize = VictoryTitleSize
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedTrophy()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                winner?.let {
                    Text(
                        text = "¡${it.name} ha ganado!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Puntuación: $score puntos",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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
