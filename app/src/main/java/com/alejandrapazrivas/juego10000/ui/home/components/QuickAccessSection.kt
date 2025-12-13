package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Sección de accesos rápidos con título y lista de cards navegables.
 */
@Composable
fun QuickAccessSection(
    onStatsClick: () -> Unit,
    onRulesClick: () -> Unit,
    onPlayersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        Text(
            text = stringResource(R.string.quick_access),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spaceSmall)
        )

        QuickAccessCard(
            title = stringResource(R.string.statistics),
            icon = R.drawable.ic_stats,
            onClick = onStatsClick
        )

        QuickAccessCard(
            title = stringResource(R.string.rules),
            icon = R.drawable.ic_rules,
            onClick = onRulesClick
        )

        QuickAccessCard(
            title = stringResource(R.string.manage_players),
            icon = R.drawable.ic_add_player,
            onClick = onPlayersClick
        )
    }
}
