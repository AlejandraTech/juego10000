package com.bigotitech.juego10000.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.ButtonShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

@Composable
fun MainMenuOptions(
    onNavigateToGame: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        MenuButton(
            text = stringResource(id = R.string.add_player),
            icon = R.drawable.ic_add_player,
            onClick = onNavigateToPlayers,
            primaryColor = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        MenuButton(
            text = stringResource(id = R.string.rules),
            icon = R.drawable.ic_rules,
            onClick = onNavigateToRules,
            primaryColor = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        MenuButton(
            text = stringResource(id = R.string.statistics),
            icon = R.drawable.ic_stats,
            onClick = onNavigateToStats,
            primaryColor = MaterialTheme.colorScheme.tertiary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuButton(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    primaryColor: Color
) {
    val dimensions = LocalDimensions.current
    var isPressed by remember { mutableStateOf(false) }

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 4f,
        label = "elevation_animation"
    )

    val gradientColors = listOf(
        primaryColor,
        primaryColor.copy(alpha = 0.8f)
    )

    val buttonHeight = dimensions.buttonHeight + dimensions.spaceSmall

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .shadow(
                elevation = elevation.dp,
                shape = ButtonShape,
                spotColor = primaryColor.copy(alpha = 0.3f)
            ),
        shape = ButtonShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = gradientColors,
                        startX = 0f,
                        endX = 1000f
                    )
                )
                .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeSmall)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        )
                        .padding(dimensions.spaceSmall),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.spaceMedium))

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}