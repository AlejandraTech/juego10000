package com.bigotitech.juego10000.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bigotitech.juego10000.data.preferences.UserPreferencesManager
import com.bigotitech.juego10000.ui.common.theme.Juego10000Theme
import com.bigotitech.juego10000.ui.navigation.AppNavigation
import com.bigotitech.juego10000.util.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val language by userPreferencesManager.language.collectAsState(initial = UserPreferencesManager.LANGUAGE_SYSTEM)

            LaunchedEffect(language) {
                LocaleHelper.setLocale(language)
            }

            Juego10000Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
