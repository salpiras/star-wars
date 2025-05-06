package io.salpiras.starwars.feature.planetlist

import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.salpiras.starwars.core.domain.usecase.GetPlanetsUseCase
import io.salpiras.starwars.core.domain.usecase.ObservePlanetsUseCase
import io.salpiras.starwars.core.model.Climate
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val observePlanetsUseCase: ObservePlanetsUseCase = mockk()
    private val getPlanetsUseCase: GetPlanetsUseCase = mockk()

    private val fakePlanets = listOf<Planet>(
        Planet(
            uid = "1",
            name = "Tattooine",
            population = 100000L,
            climate = setOf(Climate.ARID),
            diameter = 1000L,
            gravity = "1 standard",
            terrain = setOf()
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `GIVEN no planets WHEN error refreshing data THEN uiState is error`() = runTest {
        coEvery { observePlanetsUseCase() } returns flowOf(emptyList())
        coEvery { getPlanetsUseCase() } returns OpResult.Error("Error!")

        val vm = PlanetListViewModel(observePlanetsUseCase, getPlanetsUseCase)

        vm.uiState.test {
            assertEquals(PlanetListUiState.Loading, awaitItem())

            assertEquals(PlanetListUiState.Error, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `GIVEN planets WHEN success refreshing data THEN correct data retrieved`() = runTest {
        val planetsFlow = MutableStateFlow(emptyList<Planet>())
        coEvery { observePlanetsUseCase() } returns planetsFlow
        coEvery { getPlanetsUseCase() } returns OpResult.Success

        val vm = PlanetListViewModel(observePlanetsUseCase, getPlanetsUseCase)

        vm.uiState.test {
            assertEquals(PlanetListUiState.Loading, awaitItem())

            planetsFlow.emit(fakePlanets)

            val state = awaitItem()
            assertTrue(state is PlanetListUiState.Loaded)
            (state as PlanetListUiState.Loaded).apply {
                assertEquals(data.first().name, fakePlanets.first().name)
                assertEquals(data.first().population, fakePlanets.first().population)
                // This test can be expanded to verify mappings
            }
            ensureAllEventsConsumed()
        }
    }
}