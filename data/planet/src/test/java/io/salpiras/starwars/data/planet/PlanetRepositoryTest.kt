package io.salpiras.starwars.data.planet

import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.salpiras.starwars.core.model.Climate
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import io.salpiras.starwars.data.planet.network.PlanetNetworkDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val networkDataSource: PlanetNetworkDataSource = mockk()

    private val fakePlanets = listOf<Planet>(
        Planet(
            uid = "1",
            name = "Tattooine",
            population = 100000L,
            climate = setOf(Climate.ARID),
            diameter = 1000L,
            gravity = "1 standard",
            terrain = setOf()
        ),
        Planet(
            uid = "2",
            name = "Naboo",
            population = 1020000L,
            climate = setOf(Climate.TEMPERATE),
            diameter = 10200L,
            gravity = "2 standard",
            terrain = setOf()
        )
    )

    private val fakePlanetsMore = listOf<Planet>(
        Planet(
            uid = "3",
            name = "Alderan",
            population = 1324234,
            climate = setOf(Climate.ARID),
            diameter = 1000L,
            gravity = "1 standard",
            terrain = setOf()
        ),
        Planet(
            uid = "4",
            name = "Yavin IV",
            population = 1020000L,
            climate = setOf(Climate.TEMPERATE, Climate.TROPICAL),
            diameter = 10200L,
            gravity = "2 standard",
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
    fun `WHEN No planets are fetched from the remote THEN emit empty flow`() = runTest {
        coEvery { networkDataSource.getPlanets() } returns Result.success(fakePlanets)

        val repository = PlanetRepositoryImpl(networkDataSource, testDispatcher)

        repository.observePlanets().test {
            assertEquals(emptyList<Planet>(), awaitItem())
        }
    }

    @Test
    fun `WHEN Planets are fetched from the remote THEN emit correct flow`() = runTest {
        coEvery { networkDataSource.getPlanets() } returns Result.success(fakePlanets)

        val repository = PlanetRepositoryImpl(networkDataSource, testDispatcher)

        repository.observePlanets().test {
            assertEquals(emptyList<Planet>(), awaitItem())
            repository.refreshPlanets()
            assertEquals(fakePlanets, awaitItem())
        }
    }

    @Test
    fun `WHEN Planets are fetched from the remote multiple times AND the remote changed the data THEN emit correct new data`() =
        runTest {
            coEvery { networkDataSource.getPlanets() } returnsMany listOf(
                Result.success(fakePlanets),
                Result.success(fakePlanetsMore)
            )

            val repository = PlanetRepositoryImpl(networkDataSource, testDispatcher)

            repository.observePlanets().test {
                assertEquals(emptyList<Planet>(), awaitItem())
                val firstCallResult = repository.refreshPlanets()
                assertEquals(OpResult.Success, firstCallResult)
                assertEquals(fakePlanets, awaitItem())
                repository.refreshPlanets()
                val subsequentCallResult = repository.refreshPlanets()
                assertEquals(OpResult.Success, subsequentCallResult)
                assertEquals(fakePlanetsMore, awaitItem())
            }
        }

    @Test
    fun `WHEN Fetching from remote errors THEN correct error result is returned`() = runTest {
        val message = "No network"
        coEvery { networkDataSource.getPlanets() } returns Result.failure(Exception(message))

        val repository = PlanetRepositoryImpl(networkDataSource, testDispatcher)

        repository.observePlanets().test {
            assertEquals(emptyList<Planet>(), awaitItem())
            val fetchResult = repository.refreshPlanets()
            assertEquals(OpResult.Error(message), fetchResult)
        }
    }
}