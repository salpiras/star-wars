package io.salpiras.starwars.feature.planetdetail

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockk
import io.salpiras.core.design.theme.StarWarsTheme
import io.salpiras.starwars.core.domain.usecase.ObservePlanetUseCase
import io.salpiras.starwars.core.model.Climate
import io.salpiras.starwars.core.model.Planet
import io.salpiras.starwars.core.model.Terrain
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlanetViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val observePlanetUseCase: ObservePlanetUseCase = mockk()
    private val backstackEntry: NavBackStackEntry = mockk()

    private val fakePlanet = Planet(
        uid = "1",
        name = "Tatooine",
        population = 100000L,
        climate = setOf(Climate.ARID),
        diameter = 1000L,
        gravity = "1 standard",
        terrain = setOf(Terrain.DESERT, Terrain.CANYONS)
    )

    @Before
    fun setUp() {
        every { observePlanetUseCase(any()) } returns MutableStateFlow(fakePlanet)
    }

    @Test
    fun planetScreen_showPlanetInfo() {
        composeTestRule.apply {
            val context = InstrumentationRegistry
                .getInstrumentation()
                .targetContext
            setContent {
                StarWarsTheme {
                    PlanetDestination(
                        backstackEntry,
                        viewModel = PlanetViewModel(
                            observePlanetUseCase,
                            savedStateHandle = SavedStateHandle(mapOf("uid" to fakePlanet.uid))
                        )
                    )
                }
            }
            onNodeWithText(fakePlanet.name)
            onNodeWithText(context.getString(R.string.planet_detail_label_terrain))
            onNodeWithText(context.getString(R.string.planet_detail_label_climate))
            onNodeWithText(fakePlanet.gravity)
            onNodeWithText(fakePlanet.population.toString())
        }
    }
}