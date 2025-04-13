package com.alejandrapazrivas.juego10000.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.Juego10000Theme
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
                .padding(16.dp)
        )
    }
}

/**
 * Barra superior de la pantalla de configuraciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(navController: NavController) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
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

        Spacer(modifier = Modifier.height(16.dp))

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
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    Juego10000Theme {
        SettingsScreen(navController = rememberNavController())
    }
}
