package io.salpiras.starwars.feature.planetdetail

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import io.salpiras.core.design.theme.StarWarsTheme
import io.salpiras.core.design.theme.Typography

@Composable
fun PlanetDestination(
    handle: NavBackStackEntry,
    viewModel: PlanetViewModel = hiltViewModel(handle)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        PlanetUiState.Loading -> LoadingView()
        is PlanetUiState.Loaded -> PlanetView(state.planet)
    }
}

@Composable
fun PlanetView(planet: PlanetUiOverview) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .defaultMinSize(minHeight = 120.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = planet.name,
                style = Typography.headlineLarge
            )
        }
        InfoRow(
            label = "Population",
            value = planet.population?.toString() ?: "Unknown",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        InfoRow(
            label = "Diameter",
            value = planet.diameter?.toString() ?: "Unknown",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        InfoRow(
            label = "Gravity",
            value = planet.gravity.toString(),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        InfoSetView(label = "Climate", values = planet.climate)
        InfoSetView(label = "Terrain", values = planet.terrain)
    }
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1.0F)) {
            Text(
                text = label,
                style = Typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .fillMaxWidth()
            )
        }
        Column(modifier = Modifier.weight(1.0F)) {
            Text(
                text = value,
                style = Typography.labelLarge,
                modifier =
                    Modifier
                        .fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoSetView(label: String, values: Set<String>) {
    Column {
        Text(text = label, style = Typography.headlineSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            values.forEach { v ->
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = v, style = Typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlanetListItemPreview() {
    val planet = PlanetUiOverview(
        name = "Naboo",
        climate = setOf("arid, temperate"),
        population = 1234234234L,
        diameter = 10000L,
        gravity = "1.0 standard",
        terrain = setOf("Mountains", "Plains")
    )
    StarWarsTheme {
        PlanetView(planet = planet)
    }
}