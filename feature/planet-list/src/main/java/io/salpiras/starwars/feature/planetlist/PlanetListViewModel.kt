package io.salpiras.starwars.feature.planetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.salpiras.starwars.core.domain.usecase.GetPlanetsUseCase
import io.salpiras.starwars.core.domain.usecase.ObservePlanetsUseCase
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanetListViewModel @Inject constructor(
    observePlanetsUseCase: ObservePlanetsUseCase,
    private val getPlanetsUseCase: GetPlanetsUseCase
) : ViewModel() {

    private val _refreshState: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val refreshState: SharedFlow<UiEvent> = _refreshState.asSharedFlow()

    val uiState: StateFlow<PlanetListUiState> = observePlanetsUseCase()
        .map { it ->
            if (it.isEmpty()) {
                PlanetListUiState.Loading
            } else {
                PlanetListUiState.Loaded(data = it.toListItems()) // TODO: check if we can add to it at each page
            }
        }.onStart {
            loadData()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlanetListUiState.Loading,
        )

    fun loadData() {
        viewModelScope.launch {
            _refreshState.emit(UiEvent.RefreshStarted)
            when (getPlanetsUseCase()) {
                is OpResult.Error -> {
                    _refreshState.emit(UiEvent.RefreshError)
                }

                else -> {}
            }
        }
    }
}

private fun Planet.toListItem(): PlanetUiItem =
    PlanetUiItem(
        uid = uid,
        name = name,
        population = population,
        climate = climate.map { climate -> climate.toString() }.toSet()
    )

private fun List<Planet>.toListItems(): List<PlanetUiItem> = this.map { it -> it.toListItem() }