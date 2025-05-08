package io.salpiras.starwars.feature.planetlist

import androidx.compose.runtime.Immutable

sealed interface PlanetListUiState {
    data object Loading : PlanetListUiState
    data class Loaded(val data: List<PlanetUiItem>) :
        PlanetListUiState

    data object Error : PlanetListUiState
}

sealed interface UiEvent {
    data class RefreshError(val message: String?) : UiEvent
}

@Immutable
data class PlanetUiItem(
    val uid: String,
    val name: String,
    val population: Long?,
    val climate: Set<String>
)