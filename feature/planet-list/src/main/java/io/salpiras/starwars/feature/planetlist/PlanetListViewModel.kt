package io.salpiras.starwars.feature.planetlist

import android.util.Log
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
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState {
    data object Loading : UiState
    data class Loaded(val data: List<Planet>) :
        UiState // TODO: change planet to UiState stable class

    data object Error : UiState
}

sealed interface UiEvent {
    data object RefreshError : UiEvent
    data object RefreshStarted : UiEvent
}

@HiltViewModel
class PlanetListViewModel @Inject constructor(
    private val observePlanetsUseCase: ObservePlanetsUseCase,
    private val getPlanetsUseCase: GetPlanetsUseCase
) : ViewModel() {

    private val _refreshState: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val refreshState: SharedFlow<UiEvent> = _refreshState.asSharedFlow()

    // private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = observePlanetsUseCase().map { it ->
        Log.d("ViewModel", "loaded!")
        if (it.isEmpty()) {
            UiState.Loading
        } else {
            UiState.Loaded(data = it) // TODO: check if we can add to it at each page
        }
    }.onStart {
        Log.d("ViewModel", "OnStart called!")
        loadData()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading,
    )

    fun loadData() {
        Log.d("ViewModel", "loadData called!")

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