package com.bigotitech.juego10000.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bigotitech.juego10000.R
import com.bigotitech.juego10000.ui.common.theme.LocalDimensions
import com.bigotitech.juego10000.ui.common.theme.Primary
import com.bigotitech.juego10000.ui.common.theme.Secondary

data class DrawerMenuItem(
    val title: String,
    val icon: Int,
    val onClick: () -> Unit
)

@Composable
fun HomeDrawerContent(
    currentUserName: String?,
    onNavigateToPlayers: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onChangeUser: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    ModalDrawerSheet(
        modifier = modifier.width(dimensions.drawerWidth),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Header del drawer
            DrawerHeader(currentUserName = currentUserName)

            Spacer(modifier = Modifier.height(dimensions.spaceSmall))

            // Opciones principales
            DrawerMenuSection(title = stringResource(R.string.game_section)) {
                DrawerItem(
                    icon = R.drawable.ic_add_player,
                    title = stringResource(R.string.manage_players),
                    subtitle = stringResource(R.string.manage_players_description),
                    accentColor = Primary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToPlayers()
                    }
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spaceSmall))
                DrawerItem(
                    icon = R.drawable.ic_stats,
                    title = stringResource(R.string.statistics),
                    subtitle = stringResource(R.string.stats_description),
                    accentColor = Secondary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToStats()
                    }
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spaceSmall))
                DrawerItem(
                    icon = R.drawable.ic_rules,
                    title = stringResource(R.string.rules_title),
                    subtitle = stringResource(R.string.rules_description),
                    accentColor = Primary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToRules()
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Configuración
            DrawerMenuSection(title = stringResource(R.string.settings_section)) {
                DrawerItem(
                    icon = R.drawable.ic_settings,
                    title = stringResource(R.string.settings),
                    subtitle = stringResource(R.string.settings_description),
                    accentColor = Secondary,
                    onClick = {
                        onCloseDrawer()
                        onNavigateToSettings()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cambiar perfil - Sección con estilo mejorado
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimensions.spaceMedium),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            ChangeProfileItem(
                currentUserName = currentUserName,
                onClick = {
                    onCloseDrawer()
                    onChangeUser()
                }
            )

            // Footer
            DrawerFooter()
        }
    }
}

@Composable
private fun DrawerHeader(
    currentUserName: String?
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Primary,
                        Primary.copy(alpha = 0.85f),
                        Secondary.copy(alpha = 0.7f)
                    )
                )
            )
    ) {
        // Círculos decorativos de fondo
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-25).dp, y = 25.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 10.dp, y = 20.dp)
                .clip(CircleShape)
                .background(Secondary.copy(alpha = 0.3f))
        )

        // Contenido principal
        Column(
            modifier = Modifier.padding(dimensions.spaceLarge)
        ) {
            // Icono del juego con gradiente
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge + 8.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.avatarSizeLarge)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Secondary,
                                    Secondary.copy(alpha = 0.8f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dice),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(dimensions.iconSizeLarge)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = currentUserName ?: stringResource(R.string.no_user),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun DrawerMenuSection(
    title: String,
    content: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier.padding(horizontal = dimensions.spaceMedium)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = dimensions.spaceSmall, horizontal = dimensions.spaceSmall)
        )
        content()
    }
}

@Composable
private fun DrawerItem(
    icon: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cardCornerRadius))
            .clickable(onClick = onClick)
            .padding(dimensions.spaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono con gradiente de color
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeSmall)
                .clip(RoundedCornerShape(dimensions.spaceSmall + 2.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(dimensions.iconSizeSmall)
            )
        }

        Spacer(modifier = Modifier.width(dimensions.spaceMedium))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ChangeProfileItem(
    currentUserName: String?,
    onClick: () -> Unit
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Secondary.copy(alpha = 0.1f),
                            Secondary.copy(alpha = 0.05f)
                        )
                    )
                )
                .clickable(onClick = onClick)
                .padding(dimensions.spaceSmall + 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del usuario con inicial
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Secondary,
                                Secondary.copy(alpha = 0.8f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUserName?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spaceSmall + 4.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = currentUserName ?: stringResource(R.string.no_user),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.tap_to_change_profile),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Icono de cambio con fondo
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Secondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_swap),
                    contentDescription = stringResource(R.string.change_profile),
                    tint = Secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawerFooter() {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.spaceMedium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.version, "1.0"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}
