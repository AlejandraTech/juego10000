package com.bigotitech.rokub10000.ui.common.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

@Composable
fun PlayerAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val dimensions = LocalDimensions.current
    val avatarSize = size ?: dimensions.avatarSizeMedium

    Box(
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.7f),
                        backgroundColor.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.5f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
