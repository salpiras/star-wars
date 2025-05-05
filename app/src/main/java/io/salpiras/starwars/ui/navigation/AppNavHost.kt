package io.salpiras.starwars.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.salpiras.starwars.feature.planetdetail.PlanetDestination
import io.salpiras.starwars.feature.planetlist.PlanetListDestination
import kotlinx.serialization.Serializable

private sealed interface Route {
    @Serializable
    data object PlanetList : Route

    @Serializable
    data class PlanetDetail(val uid: String) : Route
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.PlanetList) {
        composable<Route.PlanetList> {
            PlanetListDestination(
                onPlanetSelected =
                    { planetId -> navController.navigate(Route.PlanetDetail(uid = planetId)) }
            )
        }
        composable<Route.PlanetDetail> { backStackEntry ->
            PlanetDestination(backStackEntry) }
    }

}