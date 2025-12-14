package com.bigotitech.rokub10000.ui.userselection.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Botón para agregar un nuevo jugador
 */
@Composable
fun AddPlayerButton(
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "add_button_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(dimensions.spaceMedium),
        color = Primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensions.spaceMedium,
                    vertical = dimensions.spaceSmall + dimensions.spaceExtraSmall
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_player),
                tint = Primary,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            Text(
                text = stringResource(R.string.new_player),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
        }
    }
}
