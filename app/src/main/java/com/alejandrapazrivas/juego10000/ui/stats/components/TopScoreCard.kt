package com.alejandrapazrivas.juego10000.ui.stats.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.ScorePositive
import com.alejandrapazrivas.juego10000.ui.common.util.DateFormatUtils
import com.alejandrapazrivas.juego10000.ui.stats.model.ScoreWithPlayer

/**
 * Tarjeta que muestra una puntuación en el ranking
 */
@Composable
fun TopScoreCard(
    scoreWithPlayer: ScoreWithPlayer,
    position: Int
) {
    val dimensions = LocalDimensions.current
    val isPodium = PodiumColors.isPodium(position)
    val positionColor = PodiumColors.getPositionColor(position)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
        ) {
            TopScoreCardHeader(
                scoreWithPlayer = scoreWithPlayer,
                position = position,
                isPodium = isPodium,
                positionColor = positionColor
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            TopScoreCardFooter(
                scoreWithPlayer = scoreWithPlayer,
                isPodium = isPodium,
                positionColor = positionColor
            )
        }
    }
}

@Composable
private fun TopScoreCardHeader(
    scoreWithPlayer: ScoreWithPlayer,
    position: Int,
    isPodium: Boolean,
    positionColor: androidx.compose.ui.graphics.Color
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PositionBadge(position = position, color = positionColor)

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        StatsPlayerAvatar(
            name = scoreWithPlayer.player.name,
            isPodium = isPodium,
            podiumColor = positionColor
        )

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scoreWithPlayer.player.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = DateFormatUtils.formatShortDate(scoreWithPlayer.score.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${scoreWithPlayer.score.turnScore}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ScorePositive
            )
            Text(
                text = stringResource(R.string.points),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TopScoreCardFooter(
    scoreWithPlayer: ScoreWithPlayer,
    isPodium: Boolean,
    positionColor: androidx.compose.ui.graphics.Color
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(dimensions.spaceMedium),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = dimensions.spaceSmall,
                    vertical = dimensions.spaceExtraSmall
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dice),
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.spaceMedium),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(dimensions.spaceExtraSmall))
                Text(
                    text = stringResource(R.string.round_number, scoreWithPlayer.score.round),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isPodium) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = null,
                tint = positionColor,
                modifier = Modifier.size(dimensions.spaceLarge)
            )
        }
    }
}
