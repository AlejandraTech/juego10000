package com.bigotitech.rokub10000.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bigotitech.rokub10000.core.audio.BackgroundMusicManager
import com.bigotitech.rokub10000.presentation.feature.game.GameScreen
import com.bigotitech.rokub10000.presentation.feature.home.HomeScreen
import com.bigotitech.rokub10000.presentation.feature.player.PlayerScreen
import com.bigotitech.rokub10000.presentation.feature.rules.RulesScreen
import com.bigotitech.rokub10000.presentation.feature.settings.SettingsScreen
import com.bigotitech.rokub10000.presentation.feature.splash.SplashScreen
import com.bigotitech.rokub10000.presentation.feature.stats.StatsScreen
import com.bigotitech.rokub10000.presentation.feature.userselection.UserSelectionScreen

/**
 * Punto de entrada principal para la navegación de la aplicación.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    backgroundMusicManager: BackgroundMusicManager? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Controlar música según la pantalla actual
    LaunchedEffect(currentRoute) {
        backgroundMusicManager?.let { musicManager ->
            val isGameScreen = currentRoute?.startsWith(Screen.Game.route) == true

            if (isGameScreen) {
                musicManager.pauseMusic()
            } else if (currentRoute != null && currentRoute != Screen.Splash.route) {
                musicManager.resumeMusic()
            }
        }
    }

    // Iniciar música cuando salimos del splash
    LaunchedEffect(currentRoute) {
        if (currentRoute == Screen.UserSelection.route) {
            backgroundMusicManager?.startMusic()
        }
    }

    // Limpiar recursos al destruir
    DisposableEffect(Unit) {
        onDispose {
            backgroundMusicManager?.stopMusic()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        addSplashScreen(navController)
        addUserSelectionScreen(navController)
        addHomeScreen(navController)
        addGameScreen(navController)
        addPlayersScreen(navController)
        addRulesScreen(navController)
        addStatsScreen(navController)
        addSettingsScreen(navController)
    }
}

/**
 * Define las rutas de navegación disponibles en la aplicación.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object UserSelection : Screen("user_selection")
    object Home : Screen("home")
    object Game : Screen("game")
    object Players : Screen("players")
    object Rules : Screen("rules")
    object Stats : Screen("stats")
    object Settings : Screen("settings")

    fun withArgs(vararg args: Any): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

private fun NavGraphBuilder.addSplashScreen(navController: NavHostController) {
    composable(Screen.Splash.route) {
        SplashScreen(
            onSplashFinished = {
                navController.navigate(Screen.UserSelection.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.addUserSelectionScreen(navController: NavHostController) {
    composable(Screen.UserSelection.route) {
        UserSelectionScreen(
            onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.UserSelection.route) { inclusive = true }
                }
            },
            onNavigateToPlayers = {
                navController.navigate(Screen.Players.route)
            }
        )
    }
}

private fun NavGraphBuilder.addHomeScreen(navController: NavHostController) {
    composable(Screen.Home.route) {
        HomeScreen(
            onNavigateToGame = { gameId ->
                navController.navigate(Screen.Game.withArgs(gameId))
            },
            onNavigateToPlayers = { navController.navigate(Screen.Players.route) },
            onNavigateToRules = { navController.navigate(Screen.Rules.route) },
            onNavigateToStats = { navController.navigate(Screen.Stats.route) },
            onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
            onNavigateToUserSelection = {
                navController.navigate(Screen.UserSelection.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.addGameScreen(navController: NavHostController) {
    composable(
        route = "${Screen.Game.route}/{gameId}",
        arguments = listOf(
            navArgument("gameId") {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) { backStackEntry ->
        val gameId = backStackEntry.arguments?.getLong("gameId") ?: 0L
        GameScreen(
            navController = navController,
            gameId = gameId
        )
    }
}

private fun NavGraphBuilder.addPlayersScreen(navController: NavHostController) {
    composable(Screen.Players.route) {
        PlayerScreen(navController = navController)
    }
}

private fun NavGraphBuilder.addRulesScreen(navController: NavHostController) {
    composable(Screen.Rules.route) {
        RulesScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addStatsScreen(navController: NavHostController) {
    composable(Screen.Stats.route) {
        StatsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addSettingsScreen(navController: NavHostController) {
    composable(Screen.Settings.route) {
        SettingsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
