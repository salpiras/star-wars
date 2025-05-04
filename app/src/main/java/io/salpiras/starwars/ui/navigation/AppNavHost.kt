package io.salpiras.starwars.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.salpiras.starwars.feature.planetlist.PlanetListDestination
import kotlinx.serialization.Serializable

private sealed interface Route {
    @Serializable
    data object PlanetList : Route

    @Serializable
    data object PlanetDetail : Route
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.PlanetList) {
        // TODO: tweak destination
        composable<Route.PlanetList> { PlanetListDestination() }
        composable<Route.PlanetDetail> { /* TBD */ }
    }

}