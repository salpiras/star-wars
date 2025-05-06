package io.salpiras.starwars.feature.planetdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.salpiras.starwars.core.domain.usecase.ObservePlanetUseCase
import io.salpiras.starwars.core.model.Climate
import io.salpiras.starwars.core.model.Planet
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class PlanetViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val observePlanetUseCase: ObservePlanetUseCase = mockk()

    private val fakePlanet = Planet(
        uid = "1",
        name = "Tattooine",
        population = 100000L,
        climate = setOf(Climate.ARID),
        diameter = 1000L,
        gravity = "1 standard",
        terrain = setOf()
    )
    private val fakeStateHandle = SavedStateHandle(initialState = mapOf("uid" to "1"))

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
    fun `WHEN no planetId passed in handle THEN throws error`() = runTest {
        assertThrows(IllegalStateException::class.java) {
            PlanetViewModel(observePlanetUseCase, SavedStateHandle())
        }
    }

    @Test
    fun `GIVEN planetId is valid WHEN vm starts observing THEN correct data is displayed`() =
        runTest {
            coEvery { observePlanetUseCase(any()) } returns flowOf(fakePlanet)

            val vm = PlanetViewModel(observePlanetUseCase, fakeStateHandle)

            vm.uiState.test {
                assertEquals(PlanetUiState.Loading, awaitItem())
                val state = awaitItem()
                assertTrue(state is PlanetUiState.Loaded)
                (state as PlanetUiState.Loaded).apply {
                    assertEquals(fakePlanet.name, planet.name)
                    assertEquals(fakePlanet.population, planet.population)
                    assertEquals(fakePlanet.gravity, planet.gravity)
                }
            }
        }
}