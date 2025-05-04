package io.salpiras.starwars.feature.planetlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.salpiras.starwars.core.model.Planet

@Composable
fun PlanetListDestination(viewModel: PlanetListViewModel = hiltViewModel()) { // add vm

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.refreshState) {
        viewModel.refreshState.collect { state ->
            when (state) {
                is UiEvent.RefreshError -> {
                    snackbarHostState.showSnackbar("Refresh error")
                }

                is UiEvent.RefreshStarted -> {
                    snackbarHostState.showSnackbar("Loading more entries..")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            when (val state = uiState) {
                UiState.Loading -> LoadingView()
                is UiState.Loaded -> PlanetListView(planets = state.data)
                UiState.Error -> ErrorView()
            }
        }
    }
}

// TODO: add uiState planet type with stable.
@Composable
fun PlanetListView(planets: List<Planet>) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        items(
            items = planets,
            key = { planet -> planet.name },
        ) { planet ->
            Text("${planet.uid} Planet: ${planet.name}")
        }
    }
}

// TODO: add lottie animation
@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Error!")
    }
}