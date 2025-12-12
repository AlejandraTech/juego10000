package com.alejandrapazrivas.juego10000.ui.userselection

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.domain.model.Player
import com.alejandrapazrivas.juego10000.ui.common.theme.CardShape
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import kotlin.math.sin

@Composable
fun UserSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    viewModel: UserSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        // Fondo animado
        AnimatedBackground()

        // Contenido
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            else -> {
                UserSelectionContent(
                    players = uiState.players,
                    showCreatePlayerHint = uiState.showCreatePlayerHint,
                    onPlayerSelected = { viewModel.selectUser(it) },
                    onCreatePlayer = onNavigateToPlayers
                )
            }
        }
    }
}

/**
 * Fondo animado con dados flotantes y gradientes
 */
@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_animation")

    // Animación para los elementos flotantes
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val primaryColor = Primary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                        primaryColor.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Círculos decorativos flotantes
            val circles = listOf(
                Triple(0.1f, 0.2f, 80.dp.toPx()),
                Triple(0.85f, 0.15f, 60.dp.toPx()),
                Triple(0.7f, 0.8f, 100.dp.toPx()),
                Triple(0.15f, 0.75f, 70.dp.toPx()),
                Triple(0.5f, 0.1f, 50.dp.toPx()),
                Triple(0.9f, 0.5f, 40.dp.toPx())
            )

            circles.forEachIndexed { index, (xRatio, yRatio, baseRadius) ->
                val offsetMultiplier = if (index % 2 == 0) floatOffset1 else floatOffset2
                val yOffset = sin(offsetMultiplier * Math.PI).toFloat() * 30.dp.toPx()

                drawCircle(
                    color = primaryColor.copy(alpha = 0.03f + (index * 0.01f)),
                    radius = baseRadius + (offsetMultiplier * 20.dp.toPx()),
                    center = Offset(
                        x = width * xRatio,
                        y = height * yRatio + yOffset
                    )
                )
            }

            // Dados decorativos (cuadrados rotados)
            val dicePositions = listOf(
                Triple(0.08f, 0.35f, 24.dp.toPx()),
                Triple(0.92f, 0.4f, 20.dp.toPx()),
                Triple(0.2f, 0.9f, 28.dp.toPx()),
                Triple(0.8f, 0.85f, 22.dp.toPx()),
                Triple(0.95f, 0.7f, 18.dp.toPx())
            )

            dicePositions.forEachIndexed { index, (xRatio, yRatio, diceSize) ->
                val diceRotation = rotation + (index * 60f)
                val yOffset = sin((floatOffset1 + index * 0.3f) * Math.PI).toFloat() * 15.dp.toPx()

                rotate(diceRotation, pivot = Offset(width * xRatio, height * yRatio + yOffset)) {
                    drawRoundRect(
                        color = primaryColor.copy(alpha = 0.08f),
                        topLeft = Offset(
                            x = width * xRatio - diceSize / 2,
                            y = height * yRatio + yOffset - diceSize / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(diceSize, diceSize),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
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

        // Header con logo animado
        AnimatedHeader()

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        if (showCreatePlayerHint || players.isEmpty()) {
            EmptyPlayersState(onCreatePlayer = onCreatePlayer)
        } else {
            // Grid de jugadores
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = dimensions.spaceSmall),
                horizontalArrangement = Arrangement.spacedBy(dimensions.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spaceMedium),
                modifier = Modifier.weight(1f)
            ) {
                items(players) { player ->
                    PlayerSelectionCard(
                        player = player,
                        onClick = { onPlayerSelected(player) }
                    )
                }

                item {
                    AddPlayerCard(onClick = onCreatePlayer)
                }
            }
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
        // Icono de dados animado
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

        Surface(
            shape = RoundedCornerShape(dimensions.spaceLarge),
            color = Primary.copy(alpha = 0.1f)
        ) {
            Text(
                text = stringResource(R.string.select_profile),
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                modifier = Modifier.padding(
                    horizontal = dimensions.spaceMedium,
                    vertical = dimensions.spaceSmall
                )
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
        targetValue = if (isPressed) 0.95f else 1f,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar con gradiente y borde
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge)
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
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Primary, Primary.copy(alpha = 0.5f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            // Nombre
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AddPlayerCard(
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "add_card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spaceMedium)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.3f),
                            Primary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(dimensions.spaceMedium)
                )
                .padding(dimensions.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de añadir
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f))
                    .border(
                        width = 2.dp,
                        color = Primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_player),
                    tint = Primary,
                    modifier = Modifier.size(dimensions.spaceExtraLarge)
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = stringResource(R.string.new_player),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(dimensions.spaceExtraSmall))

            Text(
                text = stringResource(R.string.create_profile),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyPlayersState(
    onCreatePlayer: () -> Unit
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "empty_animation")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono animado
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeLarge + dimensions.buttonHeight)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.2f),
                            Primary.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(dimensions.avatarSizeMedium + dimensions.spaceExtraSmall),
                tint = Primary
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Text(
            text = stringResource(R.string.no_players_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        Text(
            text = stringResource(R.string.no_players_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensions.spaceExtraLarge))

        // Botón de crear jugador
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .shadow(
                    elevation = dimensions.cardElevation,
                    shape = RoundedCornerShape(dimensions.spaceMedium),
                    spotColor = Primary.copy(alpha = 0.3f)
                )
                .clickable(onClick = onCreatePlayer),
            shape = RoundedCornerShape(dimensions.spaceMedium),
            colors = CardDefaults.cardColors(
                containerColor = Primary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceMedium),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(dimensions.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(dimensions.spaceSmall))
                Text(
                    text = stringResource(R.string.create_player),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
