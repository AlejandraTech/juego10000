package com.alejandrapazrivas.juego10000.ui.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ads.AdConstants
import com.alejandrapazrivas.juego10000.ui.common.components.ads.BannerAd
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.Juego10000Theme
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.settings.components.SettingItem
import com.alejandrapazrivas.juego10000.ui.settings.components.SettingsCard

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
    // Obtener el estado actual de las preferencias
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    Scaffold(
        topBar = { SettingsTopBar(navController) }
    ) { paddingValues ->
        SettingsContent(
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            darkMode = darkMode,
            onSoundEnabledChange = { viewModel.setSoundEnabled(it) },
            onVibrationEnabledChange = { viewModel.setVibrationEnabled(it) },
            onDarkModeChange = { viewModel.setDarkMode(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensions.spaceMedium)
        )
    }
}

/**
 * Barra superior de la pantalla de configuraciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(navController: NavController) {
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
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
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

/**
 * Contenido principal de la pantalla de configuraciones
 */
@Composable
private fun SettingsContent(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    darkMode: Boolean,
    onSoundEnabledChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Column(modifier = modifier) {
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

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

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

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        // Privacy Policy Card
        val context = LocalContext.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AdConstants.PRIVACY_POLICY_URL))
                    context.startActivity(intent)
                },
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
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        // Credits Card
        Card(
            modifier = Modifier.fillMaxWidth(),
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

                // First sound attribution
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://pixabay.com/sound-effects/dice-roll-201898/")
                            )
                            context.startActivity(intent)
                        }
                        .padding(vertical = dimensions.spaceExtraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sound Effect by u_ngsgp0r6zb",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Second sound attribution
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://pixabay.com/sound-effects/brass-fanfare-with-timpani-and-winchimes-reverberated-146260/")
                            )
                            context.startActivity(intent)
                        }
                        .padding(vertical = dimensions.spaceExtraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sound Effect by u_ss015dykrt",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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

        Spacer(modifier = Modifier.weight(1f))

        BannerAd(
            adUnitId = AdConstants.BANNER_SETTINGS,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    Juego10000Theme {
        SettingsScreen(navController = rememberNavController())
    }
}
