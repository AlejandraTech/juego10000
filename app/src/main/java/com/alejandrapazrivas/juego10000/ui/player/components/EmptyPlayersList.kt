package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.ButtonShape

@Composable
fun EmptyPlayersList(onAddPlayer: () -> Unit) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.emptyStateIconSize)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(dimensions.spaceMedium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_player),
                contentDescription = null,
                modifier = Modifier.size(dimensions.buttonHeight + dimensions.spaceMedium),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Text(
            text = stringResource(id = R.string.no_players),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = "Añade jugadores para comenzar a jugar",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        Button(
            onClick = onAddPlayer,
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = dimensions.spaceExtraSmall
            ),
            modifier = Modifier
                .height(dimensions.avatarSizeMedium)
                .padding(horizontal = dimensions.spaceExtraLarge)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = dimensions.spaceSmall + dimensions.spaceExtraSmall)
            )
            Text(
                text = stringResource(id = R.string.add_player),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}