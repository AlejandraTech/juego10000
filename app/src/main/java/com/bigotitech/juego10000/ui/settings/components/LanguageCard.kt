package com.bigotitech.juego10000.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.data.preferences.UserPreferencesManager
import com.bigotitech.juego10000.ui.common.theme.CardShape
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions

/**
 * Tarjeta de configuración de idioma con selector dropdown
 */
@Composable
fun LanguageCard(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var expanded by remember { mutableStateOf(false) }

    val languageOptions = listOf(
        UserPreferencesManager.LANGUAGE_SYSTEM to stringResource(R.string.language_system),
        UserPreferencesManager.LANGUAGE_SPANISH to stringResource(R.string.language_spanish),
        UserPreferencesManager.LANGUAGE_ENGLISH to stringResource(R.string.language_english),
        UserPreferencesManager.LANGUAGE_CATALAN to stringResource(R.string.language_catalan),
        UserPreferencesManager.LANGUAGE_FRENCH to stringResource(R.string.language_french)
    )

    val currentLanguageName = languageOptions.find { it.first == currentLanguage }?.second
        ?: stringResource(R.string.language_system)

    Card(
        modifier = modifier.fillMaxWidth(),
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

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
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
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languageOptions.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = name)
                                    if (code == currentLanguage) {
                                        Spacer(modifier = Modifier.width(dimensions.spaceSmall))
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onLanguageChange(code)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Text(
                text = stringResource(R.string.restart_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
