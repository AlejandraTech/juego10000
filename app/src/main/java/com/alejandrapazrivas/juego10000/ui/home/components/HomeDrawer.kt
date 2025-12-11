package com.alejandrapazrivas.juego10000.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions

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
            DrawerMenuSection(title = "Juego") {
                DrawerItem(
                    icon = R.drawable.ic_add_player,
                    title = "Gestionar Jugadores",
                    subtitle = "Añadir, editar o eliminar",
                    onClick = {
                        onCloseDrawer()
                        onNavigateToPlayers()
                    }
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spaceSmall))
                DrawerItem(
                    icon = R.drawable.ic_stats,
                    title = "Estadísticas",
                    subtitle = "Ver historial y récords",
                    onClick = {
                        onCloseDrawer()
                        onNavigateToStats()
                    }
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spaceSmall))
                DrawerItem(
                    icon = R.drawable.ic_rules,
                    title = "Reglas del Juego",
                    subtitle = "Cómo jugar y puntuaciones",
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
            DrawerMenuSection(title = "Ajustes") {
                DrawerItem(
                    icon = R.drawable.ic_settings,
                    title = "Configuración",
                    subtitle = "Sonido, vibración y más",
                    onClick = {
                        onCloseDrawer()
                        onNavigateToSettings()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cambiar perfil - Sección separada con estilo diferente
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
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(dimensions.spaceLarge)
    ) {
        Column {
            // Icono del juego
            Box(
                modifier = Modifier
                    .size(dimensions.avatarSizeLarge)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dice),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(dimensions.iconSizeLarge + dimensions.spaceExtraSmall)
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spaceMedium))

            Text(
                text = currentUserName ?: "Sin usuario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Juego 10000",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
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
        Box(
            modifier = Modifier
                .size(dimensions.avatarSizeSmall)
                .clip(RoundedCornerShape(dimensions.spaceSmall + 2.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(dimensions.iconSizeSmall)
            )
        }

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = dimensions.spaceMedium, vertical = dimensions.spaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar pequeño del usuario actual
        Box(
            modifier = Modifier
                .size(dimensions.iconSizeLarge + dimensions.spaceExtraSmall)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentUserName?.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = currentUserName ?: "Sin usuario",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Toca para cambiar de perfil",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_swap),
            contentDescription = "Cambiar perfil",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(dimensions.iconSizeSmall)
        )
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
            text = "Versión 1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}
