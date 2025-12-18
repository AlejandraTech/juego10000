package com.bigotitech.rokub10000.presentation.feature.rules.state

/**
 * Estado de la UI para la pantalla de reglas.
 *
 * @property visibleSections Conjunto de secciones actualmente visibles
 */
data class RulesUiState(
    val visibleSections: Set<RulesSection> = emptySet()
) {
    /**
     * Comprueba si una sección específica es visible.
     */
    fun isSectionVisible(section: RulesSection): Boolean = section in visibleSections

    /**
     * Muestra una sección específica.
     */
    fun showSection(section: RulesSection): RulesUiState =
        copy(visibleSections = visibleSections + section)
}

/**
 * Secciones de la pantalla de reglas.
 */
enum class RulesSection {
    HEADER,
    OBJECTIVE,
    ENTRANCE,
    MECHANICS,
    SCORING,
    STRATEGIES
}
