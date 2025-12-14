package com.bigotitech.rokub10000.ui.common.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.ButtonShape

@Composable
fun DialogButtons(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = stringResource(id = R.string.confirm),
    confirmEnabled: Boolean = true,
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    confirmTextColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        OutlinedButton(
            onClick = onCancel,
            shape = ButtonShape,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .weight(1f)
                .height(dimensions.buttonHeight)
        ) {
            Text(
                text = stringResource(id = R.string.cancel),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Button(
            onClick = onConfirm,
            enabled = confirmEnabled,
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = confirmColor,
                disabledContainerColor = confirmColor.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .weight(1f)
                .height(dimensions.buttonHeight)
        ) {
            Text(
                text = confirmText,
                color = confirmTextColor
            )
        }
    }
}
