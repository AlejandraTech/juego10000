package com.alejandrapazrivas.juego10000.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ads.AdConstants
import com.alejandrapazrivas.juego10000.ui.common.components.ads.BannerAd
import com.alejandrapazrivas.juego10000.ui.common.theme.Juego10000Theme
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.settings.components.CreditsCard
import com.alejandrapazrivas.juego10000.ui.settings.components.LanguageCard
import com.alejandrapazrivas.juego10000.ui.settings.components.PrivacyPolicyCard
import com.alejandrapazrivas.juego10000.ui.settings.components.SettingsCard
import com.alejandrapazrivas.juego10000.ui.settings.components.SettingsTopAppBar
import com.alejandrapazrivas.juego10000.ui.settings.components.SoundAttribution
import com.alejandrapazrivas.juego10000.ui.settings.model.SettingItem

/**
 * Pantalla de configuraciones de la aplicación
 * Permite al usuario modificar preferencias como sonido, vibración y modo oscuro
 */
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val dimensions = LocalDimensions.current
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val language by viewModel.language.collectAsState()

    Scaffold(
        topBar = { SettingsTopAppBar(onBackClick = { navController.navigateUp() }) }
    ) { paddingValues ->
        SettingsContent(
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            darkMode = darkMode,
            language = language,
            onSoundEnabledChange = { viewModel.setSoundEnabled(it) },
            onVibrationEnabledChange = { viewModel.setVibrationEnabled(it) },
            onDarkModeChange = { viewModel.setDarkMode(it) },
            onLanguageChange = { viewModel.setLanguage(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensions.spaceMedium)
        )
    }
}

/**
 * Contenido principal de la pantalla de configuraciones
 */
@Composable
private fun SettingsContent(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    darkMode: Boolean,
    language: String,
    onSoundEnabledChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val soundAttributions = remember {
        listOf(
            SoundAttribution(
                textResId = R.string.sound_attribution_1,
                url = "https://pixabay.com/sound-effects/dice-roll-201898/"
            ),
            SoundAttribution(
                textResId = R.string.sound_attribution_2,
                url = "https://pixabay.com/sound-effects/brass-fanfare-with-timpani-and-winchimes-reverberated-146260/"
            )
        )
    }

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            GamePreferencesSection(
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled,
                onSoundEnabledChange = onSoundEnabledChange,
                onVibrationEnabledChange = onVibrationEnabledChange
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            AppearanceSection(
                darkMode = darkMode,
                onDarkModeChange = onDarkModeChange
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            LanguageCard(
                currentLanguage = language,
                onLanguageChange = onLanguageChange
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            PrivacyPolicyCard(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AdConstants.PRIVACY_POLICY_URL))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            CreditsCard(
                attributions = soundAttributions,
                onAttributionClick = { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))
        }

        BannerAd(
            adUnitId = AdConstants.BANNER_SETTINGS,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GamePreferencesSection(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onSoundEnabledChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit
) {
    SettingsCard(
        title = stringResource(R.string.game_preferences),
        settings = listOf(
            SettingItem(
                title = stringResource(R.string.sound_effects),
                description = stringResource(R.string.enable_sound_effects),
                checked = soundEnabled,
                onCheckedChange = onSoundEnabledChange
            ),
            SettingItem(
                title = stringResource(R.string.vibration),
                description = stringResource(R.string.enable_vibration),
                checked = vibrationEnabled,
                onCheckedChange = onVibrationEnabledChange
            )
        )
    )
}

@Composable
private fun AppearanceSection(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    SettingsCard(
        title = stringResource(R.string.appearance),
        settings = listOf(
            SettingItem(
                title = stringResource(R.string.dark_mode),
                description = stringResource(R.string.enable_dark_mode),
                checked = darkMode,
                onCheckedChange = onDarkModeChange
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    Juego10000Theme {
        SettingsScreen(navController = rememberNavController())
    }
}
