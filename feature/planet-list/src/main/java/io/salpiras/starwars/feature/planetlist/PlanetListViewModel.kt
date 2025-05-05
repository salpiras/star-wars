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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanetListViewModel @Inject constructor(
    private val observePlanetsUseCase: ObservePlanetsUseCase,
    private val getPlanetsUseCase: GetPlanetsUseCase
) : ViewModel() {

    private val _refreshState: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val refreshState: SharedFlow<UiEvent> = _refreshState.asSharedFlow()

    private val _uiState: MutableStateFlow<PlanetListUiState> =
        MutableStateFlow(PlanetListUiState.Loading)
    val uiState: StateFlow<PlanetListUiState> = _uiState.asStateFlow()

    init {
        observePlanets()
    }

    private fun observePlanets() {
        observePlanetsUseCase.invoke()
            .onEach { it ->
                if (it.isNotEmpty()) {
                    _uiState.value = PlanetListUiState.Loaded(data = it.toListItems())
                }
            }
            .catch {
                _uiState.value = PlanetListUiState.Error
            }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            when (val result = getPlanetsUseCase()) {
                is OpResult.Error -> {
                    if (uiState.value !is PlanetListUiState.Loaded) {
                        _uiState.value = PlanetListUiState.Error
                    } else {
                        _refreshState.emit(UiEvent.RefreshError(result.message))
                    }
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
        climate = climate.map { climate -> climate.toString().replace("_", " ") }.toSet()
    )

private fun List<Planet>.toListItems(): List<PlanetUiItem> = this.map { it -> it.toListItem() }