package com.bigotitech.rokub10000.presentation.common.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.presentation.common.components.option.SelectableOption
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions

/**
 * Diálogo para seleccionar el idioma de la aplicación.
 * Sigue el mismo estilo que el diálogo de selección de modo de juego.
 */
@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val dimensions = LocalDimensions.current
    var selectedLanguage by remember(currentLanguage) { mutableStateOf(currentLanguage) }

    val languageOptions = listOf(
        LanguageOption(
            code = UserPreferencesManager.LANGUAGE_SYSTEM,
            nameRes = R.string.language_system,
            descriptionRes = R.string.language_system_description
        ),
        LanguageOption(
            code = UserPreferencesManager.LANGUAGE_SPANISH,
            nameRes = R.string.language_spanish,
            descriptionRes = R.string.language_spanish_description
        ),
        LanguageOption(
            code = UserPreferencesManager.LANGUAGE_ENGLISH,
            nameRes = R.string.language_english,
            descriptionRes = R.string.language_english_description
        ),
        LanguageOption(
            code = UserPreferencesManager.LANGUAGE_CATALAN,
            nameRes = R.string.language_catalan,
            descriptionRes = R.string.language_catalan_description
        ),
        LanguageOption(
            code = UserPreferencesManager.LANGUAGE_FRENCH,
            nameRes = R.string.language_french,
            descriptionRes = R.string.language_french_description
        )
    )

    BaseDialog(onDismiss = onDismiss) {
        DialogHeader(
            title = stringResource(R.string.select_language),
            description = stringResource(R.string.select_language_description),
            iconPainter = painterResource(id = R.drawable.ic_language)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            languageOptions.forEachIndexed { index, option ->
                SelectableOption(
                    title = stringResource(option.nameRes),
                    description = stringResource(option.descriptionRes),
                    isSelected = selectedLanguage == option.code,
                    onClick = { selectedLanguage = option.code },
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < languageOptions.lastIndex) {
                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        DialogButtons(
            onCancel = onDismiss,
            onConfirm = {
                onLanguageSelected(selectedLanguage)
                onDismiss()
            },
            confirmText = stringResource(R.string.confirm)
        )
    }
}

private data class LanguageOption(
    val code: String,
    val nameRes: Int,
    val descriptionRes: Int
)
