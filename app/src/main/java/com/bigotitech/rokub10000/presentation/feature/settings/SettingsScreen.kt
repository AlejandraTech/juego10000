package com.bigotitech.rokub10000.presentation.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.core.ads.AdConstants
import com.bigotitech.rokub10000.presentation.common.components.ads.BannerAd
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.feature.settings.state.SettingItem
import com.bigotitech.rokub10000.presentation.feature.settings.state.SoundAttribution

private const val PRIVACY_POLICY_URL = "https://your-privacy-policy-url.com"

/**
 * Pantalla de configuraciones de la aplicación.
 * Permite al usuario modificar preferencias como sonido, vibración, música y modo oscuro.
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val dimensions = LocalDimensions.current
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val musicEnabled by viewModel.musicEnabled.collectAsState()
    val musicVolume by viewModel.musicVolume.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val language by viewModel.language.collectAsState()

    Scaffold(
        topBar = { SettingsTopAppBar(onBackClick = onNavigateBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsContent(
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled,
                musicEnabled = musicEnabled,
                musicVolume = musicVolume,
                darkMode = darkMode,
                language = language,
                onSoundEnabledChange = { viewModel.setSoundEnabled(it) },
                onVibrationEnabledChange = { viewModel.setVibrationEnabled(it) },
                onMusicEnabledChange = { viewModel.setMusicEnabled(it) },
                onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                onDarkModeChange = { viewModel.setDarkMode(it) },
                onLanguageChange = { viewModel.setLanguage(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(paddingValues)
                    .padding(dimensions.spaceMedium)
            )

            BannerAd(
                adUnitId = AdConstants.BANNER_SETTINGS,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ==================== Top App Bar ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(onBackClick: () -> Unit) {
    val dimensions = LocalDimensions.current
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.shadow(elevation = dimensions.spaceExtraSmall)
    )
}

// ==================== Settings Content ====================

@Composable
private fun SettingsContent(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    musicEnabled: Boolean,
    musicVolume: Float,
    darkMode: Boolean,
    language: String,
    onSoundEnabledChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onMusicEnabledChange: (Boolean) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
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

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        GamePreferencesSection(
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            onSoundEnabledChange = onSoundEnabledChange,
            onVibrationEnabledChange = onVibrationEnabledChange
        )

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        MusicSettingsCard(
            musicEnabled = musicEnabled,
            musicVolume = musicVolume,
            onMusicEnabledChange = onMusicEnabledChange,
            onMusicVolumeChange = onMusicVolumeChange
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
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
}

// ==================== Game Preferences Section ====================

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

// ==================== Appearance Section ====================

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

// ==================== Settings Card ====================

@Composable
private fun SettingsCard(
    title: String,
    settings: List<SettingItem>,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            settings.forEachIndexed { index, item ->
                SettingRow(
                    title = item.title,
                    description = item.description,
                    checked = item.checked,
                    onCheckedChange = item.onCheckedChange
                )

                if (index < settings.size - 1) {
                    Spacer(modifier = Modifier.height(dimensions.spaceMedium))
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// ==================== Music Settings Card ====================

@Composable
private fun MusicSettingsCard(
    musicEnabled: Boolean,
    musicVolume: Float,
    onMusicEnabledChange: (Boolean) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.background_music),
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
                        text = stringResource(R.string.background_music),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.enable_background_music),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Switch(
                    checked = musicEnabled,
                    onCheckedChange = onMusicEnabledChange
                )
            }

            if (musicEnabled) {
                Spacer(modifier = Modifier.height(dimensions.spaceMedium))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.music_volume),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(dimensions.spaceSmall))

                    Slider(
                        value = musicVolume,
                        onValueChange = onMusicVolumeChange,
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    }
}

// ==================== Language Card ====================

@Composable
private fun LanguageCard(
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
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
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
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
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
            onLanguageSelected = { language ->
                onLanguageChange(language)
                showDialog = false
            }
        )
    }
}

// ==================== Language Selection Dialog ====================

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        UserPreferencesManager.LANGUAGE_SYSTEM to stringResource(R.string.language_system),
        UserPreferencesManager.LANGUAGE_SPANISH to stringResource(R.string.language_spanish),
        UserPreferencesManager.LANGUAGE_ENGLISH to stringResource(R.string.language_english),
        UserPreferencesManager.LANGUAGE_CATALAN to stringResource(R.string.language_catalan),
        UserPreferencesManager.LANGUAGE_FRENCH to stringResource(R.string.language_french)
    )

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.select_language),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = LocalDimensions.current.spaceSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = currentLanguage == code,
                            onClick = { onLanguageSelected(code) }
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = LocalDimensions.current.spaceSmall)
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// ==================== Privacy Policy Card ====================

@Composable
private fun PrivacyPolicyCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.legal),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.privacy_policy_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ==================== Credits Card ====================

@Composable
private fun CreditsCard(
    attributions: List<SoundAttribution>,
    onAttributionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.credits),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = stringResource(R.string.sound_credits),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            attributions.forEach { attribution ->
                LinkRow(
                    text = stringResource(attribution.textResId),
                    onClick = { onAttributionClick(attribution.url) }
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            Text(
                text = stringResource(R.string.pixabay_attribution),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun LinkRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensions.spaceExtraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}
