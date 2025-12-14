package com.bigotitech.rokub10000.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Item individual de estadística rápida con icono circular y valores.
 */
@Composable
fun QuickStatItem(
    value: String,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeMedium)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
