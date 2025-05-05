package io.salpiras.starwars.feature.planetlist

import androidx.compose.runtime.Stable

sealed interface PlanetListUiState {
    data object Loading : PlanetListUiState
    data class Loaded(val data: List<PlanetUiItem>) :
        PlanetListUiState

    data object Error : PlanetListUiState
}

sealed interface UiEvent {
    data object RefreshError : UiEvent
    data object RefreshStarted : UiEvent
}

@Stable
data class PlanetUiItem(
    val uid: String,
    val name: String,
    val population: Long?,
    val climate: Set<String>
)