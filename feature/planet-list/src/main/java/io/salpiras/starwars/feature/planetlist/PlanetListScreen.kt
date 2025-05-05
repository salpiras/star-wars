package io.salpiras.starwars.feature.planetlist

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.salpiras.core.design.theme.StarWarsTheme
import io.salpiras.core.design.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetListDestination(
    viewModel: PlanetListViewModel = hiltViewModel(),
    onPlanetSelected: (String) -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.refreshState) {
        viewModel.refreshState.collect { state ->
            when (state) {
                is UiEvent.RefreshError -> {
                    snackbarHostState.showSnackbar("Refresh error. ${state.message ?: ""}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Star Wars Planets")
            })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            when (val state = uiState) {
                PlanetListUiState.Loading -> LoadingView()
                is PlanetListUiState.Loaded -> PlanetListView(
                    planets = state.data,
                    onPlanetSelected = onPlanetSelected
                )

                is PlanetListUiState.Error -> ErrorView()
            }
        }
    }
}

@Composable
fun PlanetListView(
    planets: List<PlanetUiItem>,
    onPlanetSelected: (String) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        items(
            items = planets,
            key = { planet -> planet.name },
        ) { planet -> PlanetListItem(planet = planet, onClicked = onPlanetSelected) }
    }
}

@Composable
fun PlanetListItem(
    planet: PlanetUiItem,
    onClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClicked(planet.uid) }
            .padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1.0F)) {
                Text(
                    text = planet.name,
                    style = Typography.titleLarge,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(0.5F)
            ) {
                Text(
                    text = planet.climate.joinToString(" "), // change to chips
                    textAlign = TextAlign.End,
                    style = Typography.labelLarge,
                    modifier =
                        Modifier
                            .padding(8.dp),
                )
            }
        }
        Text(
            text = "Population: ${planet.population?.toString() ?: "Unknown"}", // TODO: localise,
            style = Typography.labelLarge,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
        )
    }
}

@Composable
fun LoadingView() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("loading.json")
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(150.dp)
            )
        }
    }
}

@Composable
fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Unable to load planets",
                style = Typography.headlineSmall
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlanetListPreview() {
    val planets = listOf(
        PlanetUiItem(
            uid = "1",
            name = "Tatooine",
            population = 100000000L,
            climate = setOf("arid", "temperate")
        ),
        PlanetUiItem(
            uid = "1",
            name = "Naboo",
            population = null,
            climate = setOf("arid", "temperate")
        )
    )
    StarWarsTheme {
        PlanetListView(planets = planets, onPlanetSelected = {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlanetListItemPreview() {
    val planet = PlanetUiItem(
        uid = "1",
        name = "Tatooine",
        population = 100000000L,
        climate = setOf("arid", "temperate")
    )
    StarWarsTheme {
        PlanetListItem(planet = planet, onClicked = {})
    }
}