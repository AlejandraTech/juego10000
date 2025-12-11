package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

@Composable
fun PlayerCard(
    player: Player,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del jugador
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeMedium)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Información del jugador
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensions.spaceMedium)
                ) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Acciones
                Row {
                    IconButton(
                        onClick = onEditPlayer,
                        modifier = Modifier
                            .size(dimensions.avatarSizeSmall)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit_player),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(dimensions.iconSizeSmall)
                        )
                    }

                    Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                    IconButton(
                        onClick = onDeletePlayer,
                        modifier = Modifier
                            .size(dimensions.avatarSizeSmall)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_player),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(dimensions.iconSizeSmall)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = dimensions.spaceExtraSmall)
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Estadísticas del jugador en tarjetas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(
                    icon = R.drawable.ic_dice,
                    label = stringResource(id = R.string.games_played),
                    value = player.gamesPlayed.toString(),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                StatCard(
                    icon = R.drawable.ic_trophy,
                    label = stringResource(id = R.string.games_won),
                    value = player.gamesWon.toString(),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(dimensions.spaceSmall))

                StatCard(
                    icon = R.drawable.ic_stats,
                    label = stringResource(id = R.string.highest_score),
                    value = player.highestScore.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}