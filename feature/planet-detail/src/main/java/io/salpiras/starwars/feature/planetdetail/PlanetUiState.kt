package io.salpiras.starwars.feature.planetdetail

sealed interface PlanetUiState {
    data object Loading : PlanetUiState
    data class Loaded(val planet: PlanetUiOverview) : PlanetUiState
}

data class PlanetUiOverview(
    val name: String,
    val climate: Set<String>,
    val population: Long?,
    val diameter: Long?,
    val gravity: String,
    val terrain: Set<String>
)