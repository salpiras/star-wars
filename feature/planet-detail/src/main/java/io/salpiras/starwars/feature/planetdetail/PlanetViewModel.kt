package io.salpiras.starwars.feature.planetdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.salpiras.starwars.core.domain.usecase.ObservePlanetUseCase
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlanetViewModel @Inject constructor(
    observePlanetUseCase: ObservePlanetUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val planetId: String = checkNotNull(
        savedStateHandle.get<String>("uid")
    ) { "Planet id required" }

    val uiState: StateFlow<PlanetUiState> = observePlanetUseCase(planetId = planetId).map {
        PlanetUiState.Loaded(planet = it.toOverview())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanetUiState.Loading,
    )

}

private fun Planet.toOverview(): PlanetUiOverview =
    PlanetUiOverview(
        name = name,
        population = population,
        climate = climate.map { climate -> climate.toString() }.toSet(),
        diameter = diameter,
        gravity = gravity,
        terrain = terrain.map { terrain -> terrain.toString() }.toSet()
    )