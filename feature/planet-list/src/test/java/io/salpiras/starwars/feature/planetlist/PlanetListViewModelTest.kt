package io.salpiras.starwars.feature.planetlist

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.salpiras.starwars.core.domain.usecase.GetPlanetsUseCase
import io.salpiras.starwars.core.domain.usecase.ObservePlanetsUseCase
import io.salpiras.starwars.core.model.Diameter
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import io.salpiras.starwars.feature.planetlist.PlanetListUiState
import io.salpiras.starwars.feature.planetlist.PlanetListViewModel
import io.salpiras.starwars.feature.planetlist.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.rules.TestWatcher

/**
 * Rule to swap Dispatchers.Main to a TestDispatcher
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: org.junit.runner.Description?) {
        Dispatchers.setMain(testDispatcher)
    }
    override fun finished(description: org.junit.runner.Description?) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeUseCase: ObservePlanetsUseCase = mockk()
    private val getUseCase: GetPlanetsUseCase       = mockk()

    @Before
    fun setup() {
        // by default, network will error
        coEvery { getUseCase(any()) } returns OpResult.Error("network down")
    }

    @Test
    fun `empty DB and network fails emits Loading then Error, no snackbar`() = runTest {
        // 1) DB stream starts empty
        every { observeUseCase() } returns flowOf(emptyList())

        val vm = PlanetListViewModel(observeUseCase, getUseCase)

        // 2) Collect uiState
        vm.uiState.test {
            // first we should get the initial Loading
            assertEquals(PlanetListUiState.Loading, awaitItem())

            // give onStart { loadData() } a chance to run
            advanceUntilIdle()

            // now we should see the Error state
            assertEquals(PlanetListUiState.Error, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // 3) Because we had no data, refreshState should never fire
        vm.refreshState.test {
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `non-empty DB and network fails emits Loaded then Snackbar event`() = runTest {
        // 1) Fake one planet in the DB
        val fake = Planet(
            id         = Planet.Id("42"),
            name       = "Tatooine",
            population = "200000",
            climate    = emptySet(),
            diameter   = Diameter(10465, true),
            gravity    = "1 standard",
            terrain    = emptySet()
        )
        every { observeUseCase() } returns MutableStateFlow(listOf(fake))

        val vm = PlanetListViewModel(observeUseCase, getUseCase)

        // 2) Collect uiState
        vm.uiState.test {
            // initial Loading
            assertEquals(PlanetListUiState.Loading, awaitItem())

            // DB sends its first value—Loaded
            advanceUntilIdle() // allow onStart and combine to run
            val loaded = awaitItem()
            assertTrue(loaded is PlanetListUiState.Loaded)
            assertEquals(1, (loaded as PlanetListUiState.Loaded).data.size)
            assertEquals("Tatooine", loaded.data.first().name)
            cancelAndIgnoreRemainingEvents()
        }

        // 3) Because we already had data, we should get a transient RefreshError
        vm.refreshState.test {
            val evt = awaitItem()
            assertTrue(evt is UiEvent.RefreshError)
            assertEquals("network down", (evt as UiEvent.RefreshError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}