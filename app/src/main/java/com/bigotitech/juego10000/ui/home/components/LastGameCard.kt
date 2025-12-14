package com.bigotitech.juego10000.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.ui.common.theme.VictoryGreen
import com.bigotitech.juego10000.ui.common.theme.VictoryGreenDark
import com.bigotitech.juego10000.ui.home.model.LastGameInfo

@Composable
fun LastGameCard(
    lastGame: LastGameInfo?,
    modifier: Modifier = Modifier
) {
    if (lastGame == null) return

    val dimensions = LocalDimensions.current
    val isVictory = lastGame.isVictory
    val gradientColors = if (isVictory) {
        listOf(
            VictoryGreen,
            VictoryGreenDark
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        )
    }

    val textColor = if (isVictory) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.horizontalGradient(colors = gradientColors))
                .padding(dimensions.spaceMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSizeExtraLarge)
                        .clip(CircleShape)
                        .background(
                            if (isVictory) Color.White.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isVictory) R.drawable.ic_trophy else R.drawable.ic_dice
                        ),
                        contentDescription = null,
                        tint = if (isVictory) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceMedium))

                // Información
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isVictory) stringResource(R.string.victory_exclamation) else stringResource(R.string.last_game),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

                    val gameMode = if (lastGame.game.gameMode == "SINGLE_PLAYER") {
                        stringResource(R.string.vs_bot)
                    } else {
                        stringResource(R.string.multiplayer_mode)
                    }

                    Text(
                        text = gameMode,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.8f)
                    )

                    if (lastGame.winnerName != null) {
                        Text(
                            text = stringResource(R.string.winner_label, lastGame.winnerName),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }

                // Puntuación
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${lastGame.playerScore}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = stringResource(R.string.points_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
