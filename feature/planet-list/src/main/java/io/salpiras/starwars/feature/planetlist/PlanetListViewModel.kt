package io.salpiras.starwars.feature.planetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.salpiras.starwars.core.domain.usecase.GetPlanetsUseCase
import io.salpiras.starwars.core.domain.usecase.ObservePlanetsUseCase
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanetListViewModel @Inject constructor(
    observePlanetsUseCase: ObservePlanetsUseCase,
    private val getPlanetsUseCase: GetPlanetsUseCase
) : ViewModel() {
    private val _refreshState = MutableSharedFlow<UiEvent>()
    val refreshState: SharedFlow<UiEvent> = _refreshState.asSharedFlow()

    private val planetsFlow: StateFlow<List<Planet>> =
        observePlanetsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _refreshFailed = MutableStateFlow(false)

    val uiState: StateFlow<PlanetListUiState> = combine(
        planetsFlow,
        _refreshFailed
    ) { planets, failed ->
        when {
            planets.isEmpty() && failed -> PlanetListUiState.Error
            planets.isEmpty() -> PlanetListUiState.Loading
            else -> PlanetListUiState.Loaded(data = planets.toListItems())
        }
    }.onStart { loadData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlanetListUiState.Loading
        )

    fun loadData() {
        viewModelScope.launch {
            when (val result = getPlanetsUseCase()) {
                is OpResult.Success -> {
                    _refreshFailed.value = false
                }

                is OpResult.Error -> {
                    if (planetsFlow.value.isEmpty()) {
                        _refreshFailed.value = true
                    } else {
                        _refreshState.emit(UiEvent.RefreshError(result.message))
                    }
                }
            }
        }
    }
}

/**
 * Mappings between the model layer and the UI layer classes. This is done to achieve separations
 * of concerns and not need to tweak UI models if we want to change data in the model layer.
 * If they become more complex, usually a mapper class is injected to the viewmodel to increase
 * testability.
 */

private fun Planet.toListItem(): PlanetUiItem =
    PlanetUiItem(
        uid = uid,
        name = name,
        population = population,
        climate = climate.map { climate -> climate.toString().replace("_", " ") }.toSet()
    )

private fun List<Planet>.toListItems(): List<PlanetUiItem> = this.map { it -> it.toListItem() }