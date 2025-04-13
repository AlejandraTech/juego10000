package com.alejandrapazrivas.juego10000.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alejandrapazrivas.juego10000.ui.game.GameScreen
import com.alejandrapazrivas.juego10000.ui.home.HomeScreen
import com.alejandrapazrivas.juego10000.ui.player.PlayerScreen
import com.alejandrapazrivas.juego10000.ui.rules.RulesScreen
import com.alejandrapazrivas.juego10000.ui.settings.SettingsScreen
import com.alejandrapazrivas.juego10000.ui.stats.StatsScreen

/**
 * Punto de entrada principal para la navegación de la aplicación.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
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

private fun NavGraphBuilder.addHomeScreen(navController: NavHostController) {
    composable(Screen.Home.route) {
        HomeScreen(
            onNavigateToGame = { gameId ->
                navController.navigate(Screen.Game.withArgs(gameId))
            },
            onNavigateToPlayers = { navController.navigate(Screen.Players.route) },
            onNavigateToRules = { navController.navigate(Screen.Rules.route) },
            onNavigateToStats = { navController.navigate(Screen.Stats.route) },
            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
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
        RulesScreen(navController = navController)
    }
}

private fun NavGraphBuilder.addStatsScreen(navController: NavHostController) {
    composable(Screen.Stats.route) {
        StatsScreen(navController = navController)
    }
}

private fun NavGraphBuilder.addSettingsScreen(navController: NavHostController) {
    composable(Screen.Settings.route) {
        SettingsScreen(navController = navController)
    }
}
