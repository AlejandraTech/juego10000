package com.bigotitech.rokub10000.ui.rules.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary

/**
 * Fila moderna que muestra una regla de multiplicador.
 * Diseño limpio con bullet point.
 */
@Composable
fun MultiplierRow(text: String) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bullet point
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Primary)
        )

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
