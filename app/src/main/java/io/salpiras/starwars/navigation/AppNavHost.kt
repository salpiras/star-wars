package io.salpiras.starwars.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        composable<Route.PlanetList> { Text("Hello android!") }
        composable<Route.PlanetDetail> { /* TBD */ }
    }

}