package com.bigotitech.rokub10000.presentation.feature.userselection

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.presentation.common.components.backgrounds.AnimatedBackground
import com.bigotitech.rokub10000.presentation.common.components.backgrounds.BackgroundConfig
import com.bigotitech.rokub10000.presentation.common.components.dialog.AudioControlDialog
import com.bigotitech.rokub10000.presentation.common.components.dialog.LanguageSelectionDialog
import com.bigotitech.rokub10000.presentation.common.theme.CardShape
import com.bigotitech.rokub10000.presentation.common.theme.LocalDimensions
import com.bigotitech.rokub10000.presentation.common.theme.Primary

/**
 * Pantalla de selección de usuario.
 * Permite al usuario seleccionar su perfil antes de acceder al juego.
 *
 * @param onNavigateToHome Callback para navegar al home
 * @param onNavigateToPlayers Callback para navegar a la gestión de jugadores
 * @param viewModel ViewModel que gestiona el estado de la pantalla
 */
@Composable
fun UserSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    viewModel: UserSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLanguage by viewModel.language.collectAsState()
    val musicEnabled by viewModel.musicEnabled.collectAsState()
    val musicVolume by viewModel.musicVolume.collectAsState()
    val dimensions = LocalDimensions.current

    var showAudioDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPlayers()
    }

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            viewModel.onNavigationHandled()
            onNavigateToHome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(config = BackgroundConfig.splashConfig)

        when {
            uiState.isLoading -> LoadingState()
            else -> UserSelectionContent(
                players = uiState.players,
                showCreatePlayerHint = uiState.showCreatePlayerHint,
                onPlayerSelected = { viewModel.selectUser(it) },
                onCreatePlayer = onNavigateToPlayers
            )
        }

        // Botones de control en la esquina superior derecha
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimensions.spaceMedium)
        ) {
            IconButton(
                onClick = { showAudioDialog = true }
            ) {
                Icon(
                    imageVector = if (musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    contentDescription = stringResource(R.string.music),
                    tint = Primary
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceSmall))

            IconButton(
                onClick = { showLanguageDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = stringResource(R.string.language),
                    tint = Primary
                )
            }
        }
    }

    // Diálogos
    if (showAudioDialog) {
        AudioControlDialog(
            musicEnabled = musicEnabled,
            musicVolume = musicVolume,
            onMusicEnabledChange = { viewModel.setMusicEnabled(it) },
            onMusicVolumeChange = { viewModel.setMusicVolume(it) },
            onDismiss = { showAudioDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = {
                viewModel.setLanguage(it)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun UserSelectionContent(
    players: List<Player>,
    showCreatePlayerHint: Boolean,
    onPlayerSelected: (Player) -> Unit,
    onCreatePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimensions.buttonHeight + dimensions.spaceLarge))

        AnimatedHeader()

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        AddPlayerButton(onClick = onCreatePlayer)

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        if (showCreatePlayerHint || players.isEmpty()) {
            EmptyPlayersState()
        } else {
            PlayersList(
                players = players,
                onPlayerSelected = onPlayerSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))
    }
}

@Composable
private fun AnimatedHeader() {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeLarge)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary,
                            Primary.copy(alpha = 0.7f + shimmerOffset * 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(dimensions.avatarSizeLarge)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceMedium))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = stringResource(R.string.select_profile),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddPlayerButton(onClick: () -> Unit) {
    val dimensions = LocalDimensions.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.buttonHeight),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary.copy(alpha = 0.1f),
            contentColor = Primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(dimensions.spaceSmall))
        Text(
            text = stringResource(R.string.create_player),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyPlayersState() {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_players_yet),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensions.spaceSmall))
        Text(
            text = stringResource(R.string.create_player_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlayersList(
    players: List<Player>,
    onPlayerSelected: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    LazyColumn(
        contentPadding = PaddingValues(vertical = dimensions.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium),
        modifier = modifier
    ) {
        items(players) { player ->
            PlayerSelectionCard(
                player = player,
                onClick = { onPlayerSelected(player) }
            )
        }
    }
}

@Composable
private fun PlayerSelectionCard(
    player: Player,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = dimensions.cardElevation,
                shape = CardShape,
                spotColor = Primary.copy(alpha = 0.2f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerSelectionAvatar(name = player.name)

            Spacer(modifier = Modifier.width(dimensions.spaceMedium))

            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Primary.copy(alpha = 0.5f),
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }
    }
}

@Composable
private fun PlayerSelectionAvatar(name: String) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .size(dimensions.avatarSizeMedium)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.7f),
                        Primary.copy(alpha = 0.3f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Primary, Primary.copy(alpha = 0.5f))
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
