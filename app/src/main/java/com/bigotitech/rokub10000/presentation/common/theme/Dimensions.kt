package com.bigotitech.rokub10000.presentation.common.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tipos de ventana basados en el ancho de la pantalla
 */
enum class WindowType {
    Compact,  // Teléfonos en portrait (< 600dp)
    Medium,   // Teléfonos grandes / tablets pequeñas (600dp - 840dp)
    Expanded  // Tablets / Desktop (> 840dp)
}

/**
 * Orientación de la pantalla
 */
enum class ScreenOrientation {
    Portrait,
    Landscape
}

/**
 * Información sobre la ventana actual
 */
data class WindowInfo(
    val windowType: WindowType,
    val screenOrientation: ScreenOrientation,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp
)

/**
 * Dimensiones responsive que se escalan según el tipo de ventana
 */
data class Dimensions(
    // Spacing
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceLarge: Dp = 24.dp,
    val spaceExtraLarge: Dp = 32.dp,

    // Padding horizontal para contenido
    val screenPaddingHorizontal: Dp = 16.dp,
    val screenPaddingVertical: Dp = 8.dp,

    // Componentes - Botones
    val buttonHeight: Dp = 52.dp,
    val buttonCornerRadius: Dp = 24.dp,

    // Componentes - Dados
    val diceSize: Dp = 64.dp,
    val diceIconSize: Dp = 56.dp,
    val diceCornerRadius: Dp = 8.dp,
    val diceSpacing: Dp = 16.dp,

    // Componentes - Avatares
    val avatarSizeSmall: Dp = 40.dp,
    val avatarSizeMedium: Dp = 56.dp,
    val avatarSizeLarge: Dp = 72.dp,

    // Componentes - Iconos
    val iconSizeSmall: Dp = 20.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp,
    val iconSizeExtraLarge: Dp = 48.dp,

    // Corner radius genéricos
    val cornerRadiusSmall: Dp = 8.dp,
    val cornerRadiusMedium: Dp = 12.dp,
    val cornerRadiusLarge: Dp = 16.dp,

    // Cards
    val cardCornerRadius: Dp = 12.dp,
    val cardElevation: Dp = 4.dp,
    val cardPadding: Dp = 16.dp,

    // Elevations
    val elevationNone: Dp = 0.dp,
    val elevationSmall: Dp = 2.dp,

    // Empty state
    val emptyStateIconSize: Dp = 120.dp,

    // Indicadores
    val indicatorSize: Dp = 12.dp,
    val progressIndicatorSize: Dp = 48.dp,

    // Círculos decorativos
    val decorativeCircleTopSize: Dp = 200.dp,
    val decorativeCircleBottomSize: Dp = 180.dp,

    // Header
    val headerIconSize: Dp = 80.dp,

    // Tipografía escalada
    val scoreLarge: TextUnit = 40.sp,
    val scoreMedium: TextUnit = 36.sp,
    val scoreSmall: TextUnit = 32.sp,
    val titleFontSize: TextUnit = 18.sp,

    // Ancho máximo para contenido en tablets
    val maxContentWidth: Dp = 600.dp,

    // Dialog
    val dialogMinWidth: Dp = 280.dp,
    val dialogMaxWidth: Dp = 560.dp,

    // Drawer
    val drawerWidth: Dp = 300.dp,

    // Scoreboard
    val scoreboardMaxHeight: Dp = 180.dp,
    val scoreboardPlayerRowHeight: Dp = 56.dp,
    val scoreboardAvatarSize: Dp = 36.dp,

    // Dice area minimum height (2 rows of dice + spacing)
    val diceAreaMinHeight: Dp = 160.dp
)

/**
 * Dimensiones para pantallas compactas (teléfonos)
 */
val CompactDimensions = Dimensions(
    spaceExtraSmall = 4.dp,
    spaceSmall = 8.dp,
    spaceMedium = 16.dp,
    spaceLarge = 24.dp,
    spaceExtraLarge = 32.dp,
    screenPaddingHorizontal = 16.dp,
    screenPaddingVertical = 8.dp,
    buttonHeight = 48.dp,
    buttonCornerRadius = 24.dp,
    diceSize = 56.dp,
    diceIconSize = 48.dp,
    diceCornerRadius = 8.dp,
    diceSpacing = 12.dp,
    avatarSizeSmall = 36.dp,
    avatarSizeMedium = 48.dp,
    avatarSizeLarge = 64.dp,
    iconSizeSmall = 18.dp,
    iconSizeMedium = 22.dp,
    iconSizeLarge = 28.dp,
    iconSizeExtraLarge = 40.dp,
    cornerRadiusSmall = 6.dp,
    cornerRadiusMedium = 10.dp,
    cornerRadiusLarge = 14.dp,
    cardCornerRadius = 12.dp,
    cardElevation = 4.dp,
    cardPadding = 12.dp,
    elevationNone = 0.dp,
    elevationSmall = 2.dp,
    emptyStateIconSize = 100.dp,
    indicatorSize = 10.dp,
    progressIndicatorSize = 40.dp,
    decorativeCircleTopSize = 160.dp,
    decorativeCircleBottomSize = 140.dp,
    headerIconSize = 64.dp,
    scoreLarge = 36.sp,
    scoreMedium = 32.sp,
    scoreSmall = 28.sp,
    titleFontSize = 16.sp,
    maxContentWidth = 480.dp,
    dialogMinWidth = 280.dp,
    dialogMaxWidth = 400.dp,
    drawerWidth = 280.dp,
    scoreboardMaxHeight = 130.dp,
    scoreboardPlayerRowHeight = 48.dp,
    scoreboardAvatarSize = 30.dp,
    diceAreaMinHeight = 140.dp
)

/**
 * Dimensiones para pantallas medianas (teléfonos grandes, tablets pequeñas)
 */
val MediumDimensions = Dimensions(
    spaceExtraSmall = 6.dp,
    spaceSmall = 10.dp,
    spaceMedium = 18.dp,
    spaceLarge = 28.dp,
    spaceExtraLarge = 36.dp,
    screenPaddingHorizontal = 20.dp,
    screenPaddingVertical = 10.dp,
    buttonHeight = 52.dp,
    buttonCornerRadius = 26.dp,
    diceSize = 64.dp,
    diceIconSize = 56.dp,
    diceCornerRadius = 10.dp,
    diceSpacing = 16.dp,
    avatarSizeSmall = 40.dp,
    avatarSizeMedium = 56.dp,
    avatarSizeLarge = 72.dp,
    iconSizeSmall = 20.dp,
    iconSizeMedium = 24.dp,
    iconSizeLarge = 32.dp,
    iconSizeExtraLarge = 48.dp,
    cornerRadiusSmall = 8.dp,
    cornerRadiusMedium = 12.dp,
    cornerRadiusLarge = 16.dp,
    cardCornerRadius = 14.dp,
    cardElevation = 6.dp,
    cardPadding = 16.dp,
    elevationNone = 0.dp,
    elevationSmall = 3.dp,
    emptyStateIconSize = 120.dp,
    indicatorSize = 12.dp,
    progressIndicatorSize = 48.dp,
    decorativeCircleTopSize = 200.dp,
    decorativeCircleBottomSize = 180.dp,
    headerIconSize = 80.dp,
    scoreLarge = 40.sp,
    scoreMedium = 36.sp,
    scoreSmall = 32.sp,
    titleFontSize = 18.sp,
    maxContentWidth = 600.dp,
    dialogMinWidth = 320.dp,
    dialogMaxWidth = 480.dp,
    drawerWidth = 300.dp,
    scoreboardMaxHeight = 160.dp,
    scoreboardPlayerRowHeight = 52.dp,
    scoreboardAvatarSize = 34.dp,
    diceAreaMinHeight = 160.dp
)

/**
 * Dimensiones para pantallas expandidas (tablets, desktop)
 */
val ExpandedDimensions = Dimensions(
    spaceExtraSmall = 8.dp,
    spaceSmall = 12.dp,
    spaceMedium = 20.dp,
    spaceLarge = 32.dp,
    spaceExtraLarge = 40.dp,
    screenPaddingHorizontal = 24.dp,
    screenPaddingVertical = 12.dp,
    buttonHeight = 56.dp,
    buttonCornerRadius = 28.dp,
    diceSize = 80.dp,
    diceIconSize = 72.dp,
    diceCornerRadius = 12.dp,
    diceSpacing = 20.dp,
    avatarSizeSmall = 48.dp,
    avatarSizeMedium = 64.dp,
    avatarSizeLarge = 88.dp,
    iconSizeSmall = 24.dp,
    iconSizeMedium = 28.dp,
    iconSizeLarge = 36.dp,
    iconSizeExtraLarge = 56.dp,
    cornerRadiusSmall = 10.dp,
    cornerRadiusMedium = 14.dp,
    cornerRadiusLarge = 20.dp,
    cardCornerRadius = 16.dp,
    cardElevation = 8.dp,
    cardPadding = 20.dp,
    elevationNone = 0.dp,
    elevationSmall = 4.dp,
    emptyStateIconSize = 140.dp,
    indicatorSize = 14.dp,
    progressIndicatorSize = 56.dp,
    decorativeCircleTopSize = 240.dp,
    decorativeCircleBottomSize = 220.dp,
    headerIconSize = 96.dp,
    scoreLarge = 48.sp,
    scoreMedium = 42.sp,
    scoreSmall = 36.sp,
    titleFontSize = 20.sp,
    maxContentWidth = 840.dp,
    dialogMinWidth = 400.dp,
    dialogMaxWidth = 560.dp,
    drawerWidth = 340.dp,
    scoreboardMaxHeight = 200.dp,
    scoreboardPlayerRowHeight = 60.dp,
    scoreboardAvatarSize = 40.dp,
    diceAreaMinHeight = 200.dp
)

/**
 * CompositionLocal para acceder a las dimensiones en cualquier parte de la UI
 */
val LocalDimensions = compositionLocalOf { Dimensions() }

/**
 * CompositionLocal para acceder a la información de la ventana
 */
val LocalWindowInfo = compositionLocalOf {
    WindowInfo(
        windowType = WindowType.Compact,
        screenOrientation = ScreenOrientation.Portrait,
        screenWidthDp = 360.dp,
        screenHeightDp = 640.dp
    )
}

/**
 * Composable que calcula y recuerda la información de la ventana actual
 */
@Composable
fun rememberWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    return remember(configuration) {
        WindowInfo(
            windowType = when {
                screenWidthDp < 600.dp -> WindowType.Compact
                screenWidthDp < 840.dp -> WindowType.Medium
                else -> WindowType.Expanded
            },
            screenOrientation = if (screenWidthDp > screenHeightDp) {
                ScreenOrientation.Landscape
            } else {
                ScreenOrientation.Portrait
            },
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp
        )
    }
}

/**
 * Obtiene las dimensiones apropiadas según el tipo de ventana
 */
fun getDimensionsForWindowType(windowType: WindowType): Dimensions {
    return when (windowType) {
        WindowType.Compact -> CompactDimensions
        WindowType.Medium -> MediumDimensions
        WindowType.Expanded -> ExpandedDimensions
    }
}
