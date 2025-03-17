package com.alejandrapazrivas.juego10000.ui.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_player),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        modifier = Modifier.shadow(elevation = 4.dp)
    )
}