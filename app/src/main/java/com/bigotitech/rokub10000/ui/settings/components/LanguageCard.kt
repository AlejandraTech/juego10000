package com.bigotitech.rokub10000.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.ui.common.components.dialog.LanguageSelectionDialog
import com.bigotitech.rokub10000.ui.common.theme.CardShape
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions

/**
 * Tarjeta de configuración de idioma que muestra un diálogo al hacer clic
 */
@Composable
fun LanguageCard(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var showDialog by remember { mutableStateOf(false) }

    val currentLanguageName = when (currentLanguage) {
        UserPreferencesManager.LANGUAGE_SPANISH -> stringResource(R.string.language_spanish)
        UserPreferencesManager.LANGUAGE_ENGLISH -> stringResource(R.string.language_english)
        UserPreferencesManager.LANGUAGE_CATALAN -> stringResource(R.string.language_catalan)
        UserPreferencesManager.LANGUAGE_FRENCH -> stringResource(R.string.language_french)
        else -> stringResource(R.string.language_system)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.select_language),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currentLanguageName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }

    if (showDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showDialog = false },
            onLanguageSelected = onLanguageChange
        )
    }
}
