package com.bigotitech.juego10000.ui.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.domain.model.Player
import com.bigotitech.juego10000.ui.common.components.avatar.PlayerAvatar
import com.bigotitech.juego10000.ui.common.components.card.IconStatCard
import com.bigotitech.juego10000.ui.common.theme.CardShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

@Composable
fun PlayerCard(
    player: Player,
    bestTurnScore: Int,
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

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
            PlayerCardHeader(
                playerName = player.name,
                onEditPlayer = onEditPlayer,
                onDeletePlayer = onDeletePlayer
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            PlayerStatsRow(player = player, bestTurnScore = bestTurnScore)
        }
    }
}

@Composable
private fun PlayerCardHeader(
    playerName: String,
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAvatar(name = playerName)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensions.spaceMedium)
        ) {
            Text(
                text = playerName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        PlayerActions(
            onEditPlayer = onEditPlayer,
            onDeletePlayer = onDeletePlayer
        )
    }
}

@Composable
private fun PlayerActions(
    onEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row {
        ActionButton(
            onClick = onEditPlayer,
            icon = Icons.Default.Edit,
            contentDescription = stringResource(id = R.string.edit_player),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            iconTint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        ActionButton(
            onClick = onDeletePlayer,
            icon = Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.delete_player),
            backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            iconTint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color
) {
    val dimensions = LocalDimensions.current

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(dimensions.avatarSizeSmall)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(dimensions.iconSizeSmall)
        )
    }
}

@Composable
private fun PlayerStatsRow(player: Player, bestTurnScore: Int) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconStatCard(
            icon = R.drawable.ic_dice,
            label = stringResource(id = R.string.games_played),
            value = player.gamesPlayed.toString(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        IconStatCard(
            icon = R.drawable.ic_trophy,
            label = stringResource(id = R.string.games_won),
            value = player.gamesWon.toString(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        IconStatCard(
            icon = R.drawable.ic_stats,
            label = stringResource(id = R.string.highest_score),
            value = bestTurnScore.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}
