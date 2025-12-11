package com.alejandrapazrivas.juego10000.ui.userselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandrapazrivas.juego10000.data.preferences.UserPreferencesManager
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.domain.usecase.GetPlayersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSelectionUiState(
    val isLoading: Boolean = true,
    val players: List<Player> = emptyList(),
    val selectedUserId: Long = 0L,
    val navigateToHome: Boolean = false,
    val showCreatePlayerHint: Boolean = false
)

@HiltViewModel
class UserSelectionViewModel @Inject constructor(
    private val getPlayersUseCase: GetPlayersUseCase,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSelectionUiState())
    val uiState: StateFlow<UserSelectionUiState> = _uiState.asStateFlow()

    init {
        loadPlayersOnStart()
    }

    private fun loadPlayersOnStart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val players = getPlayersUseCase().first()
                // Siempre mostrar la pantalla de selección de usuario
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

    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToHome = false) }
    }
}
