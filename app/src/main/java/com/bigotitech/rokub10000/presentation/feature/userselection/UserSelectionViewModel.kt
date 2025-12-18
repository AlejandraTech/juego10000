package com.bigotitech.rokub10000.presentation.feature.userselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.usecase.player.GetPlayersUseCase
import com.bigotitech.rokub10000.presentation.feature.userselection.state.UserSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de selección de usuario.
 * Gestiona la carga de jugadores y la selección del usuario actual.
 */
@HiltViewModel
class UserSelectionViewModel @Inject constructor(
    private val getPlayersUseCase: GetPlayersUseCase,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSelectionUiState())
    val uiState: StateFlow<UserSelectionUiState> = _uiState.asStateFlow()

    val language: StateFlow<String> = userPreferencesManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferencesManager.LANGUAGE_SYSTEM)

    val musicEnabled: StateFlow<Boolean> = userPreferencesManager.musicEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val musicVolume: StateFlow<Float> = userPreferencesManager.musicVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)

    init {
        loadPlayersOnStart()
    }

    /**
     * Carga inicial de jugadores
     */
    private fun loadPlayersOnStart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val players = getPlayersUseCase().first()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        players = players,
                        showCreatePlayerHint = players.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showCreatePlayerHint = true
                    )
                }
            }
        }
    }

    /**
     * Carga los jugadores y observa cambios
     */
    fun loadPlayers() {
        viewModelScope.launch {
            getPlayersUseCase().collect { players ->
                _uiState.update {
                    it.copy(
                        players = players,
                        showCreatePlayerHint = players.isEmpty()
                    )
                }
            }
        }
    }

    /**
     * Selecciona un usuario y navega al home
     */
    fun selectUser(player: Player) {
        viewModelScope.launch {
            userPreferencesManager.setSelectedUserId(player.id)
            _uiState.update {
                it.copy(
                    selectedUserId = player.id,
                    navigateToHome = true
                )
            }
        }
    }

    /**
     * Marca la navegación como manejada
     */
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToHome = false) }
    }

    /**
     * Cambia el idioma de la aplicación
     */
    fun setLanguage(language: String) {
        viewModelScope.launch {
            userPreferencesManager.setLanguage(language)
        }
    }

    /**
     * Activa/desactiva la música
     */
    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setMusicEnabled(enabled)
        }
    }

    /**
     * Cambia el volumen de la música
     */
    fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            userPreferencesManager.setMusicVolume(volume)
        }
    }
}
