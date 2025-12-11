package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

/**
 * Cabecera de la pantalla principal que muestra el logo y título del juego.
 */
@Composable
fun GameHeader(
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = dimensions.spaceMedium)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_dice),
            contentDescription = null,
            modifier = Modifier.size(dimensions.headerIconSize)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimensions.spaceSmall)
        )
    }
}
