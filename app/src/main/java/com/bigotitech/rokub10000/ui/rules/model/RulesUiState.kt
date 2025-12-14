package com.bigotitech.rokub10000.ui.rules.model

data class RulesUiState(
    val visibleSections: Set<RulesSection> = emptySet()
) {
    fun isSectionVisible(section: RulesSection): Boolean = section in visibleSections

    fun showSection(section: RulesSection): RulesUiState =
        copy(visibleSections = visibleSections + section)
}

enum class RulesSection {
    HEADER,
    OBJECTIVE,
    ENTRANCE,
    MECHANICS,
    SCORING,
    STRATEGIES
}
