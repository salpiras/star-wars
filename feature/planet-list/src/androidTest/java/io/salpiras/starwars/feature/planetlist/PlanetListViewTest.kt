package io.salpiras.starwars.feature.planetlist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.coEvery
import io.salpiras.core.design.theme.StarWarsTheme
import io.salpiras.starwars.core.model.Climate
import io.salpiras.starwars.core.model.Diameter
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.every
import io.mockk.mockk
import io.salpiras.starwars.core.domain.usecase.GetPlanetsUseCase
import io.salpiras.starwars.core.domain.usecase.ObservePlanetsUseCase
import io.salpiras.starwars.core.model.OpResult

@RunWith(AndroidJUnit4::class)
class PlanetListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val observePlanetsUseCase: ObservePlanetsUseCase = mockk()
    private val getPlanetsUseCase: GetPlanetsUseCase = mockk()

    private val fakePlanets = listOf<Planet>(
        Planet(
            uid = "1",
            name = "Tatooine",
            population = 100000L,
            climate = setOf(Climate.ARID),
            diameter = 1000L,
            gravity = "1 standard",
            terrain = setOf()
        ),
        Planet(
            uid = "2",
            name = "Alderaan",
            population = 200000000L,
            climate = setOf(Climate.TEMPERATE),
            diameter = 1000L,
            gravity = "2 standard",
            terrain = setOf()
        ),
    )

    @Before
    fun setUp() {
        every { observePlanetsUseCase() } returns MutableStateFlow(fakePlanets)
        coEvery { getPlanetsUseCase() } returns OpResult.Success
    }

    @Test
    fun planetList_showsAllPlanetNames() {
        composeTestRule.setContent {
            StarWarsTheme {
                PlanetListDestination(
                    viewModel = PlanetListViewModel(observePlanetsUseCase, getPlanetsUseCase),
                    onPlanetSelected = { /* no-op */ }
                )
            }
        }

        composeTestRule.onNodeWithText(fakePlanets[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(fakePlanets[1].name).assertIsDisplayed()
    }
}

// TODO: add tap test