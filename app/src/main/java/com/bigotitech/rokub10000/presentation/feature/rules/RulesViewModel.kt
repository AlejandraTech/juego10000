package com.bigotitech.rokub10000.presentation.feature.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigotitech.rokub10000.presentation.feature.rules.state.RulesSection
import com.bigotitech.rokub10000.presentation.feature.rules.state.RulesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de reglas.
 * Gestiona la animación escalonada de las secciones de reglas.
 */
@HiltViewModel
class RulesViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val ANIMATION_DELAY_MS = 100L
    }

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    init {
        animateSections()
    }

    /**
     * Anima la aparición de las secciones de manera escalonada.
     */
    private fun animateSections() {
        viewModelScope.launch {
            RulesSection.entries.forEach { section ->
                _uiState.value = _uiState.value.showSection(section)
                delay(ANIMATION_DELAY_MS)
            }
        }
    }
}
