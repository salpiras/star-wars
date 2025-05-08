package io.salpiras.starwars.data.planet

import androidx.lifecycle.SavedStateHandle
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.core.ValueClassSupport.boxedValue
import io.mockk.mockk
import io.salpiras.starwars.core.model.Planet
import io.salpiras.starwars.data.planet.network.PlanetApiService
import io.salpiras.starwars.data.planet.network.PlanetNetworkDataSource
import io.salpiras.starwars.data.planet.network.dto.PlanetDetailResponseDto
import io.salpiras.starwars.data.planet.network.dto.PlanetDto
import io.salpiras.starwars.data.planet.network.dto.PlanetResultDto
import io.salpiras.starwars.data.planet.network.dto.PlanetSummaryDto
import io.salpiras.starwars.data.planet.network.dto.PlanetsResponseDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetNetworkDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()
    private val apiService: PlanetApiService = mockk()

    val fakePlanetsResponse = PlanetsResponseDto(
        message = "ok",
        totalRecords = 3,
        totalPages = 1,
        previous = null,
        next = null,
        results = listOf(
            PlanetSummaryDto(
                uid = "1",
                name = "Tatooine",
                url = "https://swapi.tech/api/planets/1"
            ),
            PlanetSummaryDto(
                uid = "2",
                name = "Alderaan",
                url = "https://swapi.tech/api/planets/2"
            ),
            PlanetSummaryDto(
                uid = "3",
                name = "Hoth",
                url = "https://swapi.tech/api/planets/3"
            )
        )
    )

    val fakePlanetDetails = listOf(
        PlanetDetailResponseDto(
            message = "ok",
            result = PlanetResultDto(
                uid = "1",
                properties = PlanetDto(
                    name = "Tatooine",
                    rotationPeriod = "23",
                    orbitalPeriod = "304",
                    diameter = "10465",
                    climate = "arid",
                    gravity = "1 standard",
                    terrain = "desert",
                    surfaceWater = "1",
                    population = "200000",
                    url = "https://swapi.tech/api/planets/1",
                    created = "2014-12-09T13:50:49.641000Z",
                    edited = "2014-12-20T20:58:18.411000Z"
                )
            )
        ),
        PlanetDetailResponseDto(
            message = "ok",
            result = PlanetResultDto(
                uid = "2",
                properties = PlanetDto(
                    name = "Alderaan",
                    rotationPeriod = "24",
                    orbitalPeriod = "364",
                    diameter = "12500",
                    climate = "temperate",
                    gravity = "1 standard",
                    terrain = "grasslands, mountains",
                    surfaceWater = "40",
                    population = "2000000000",
                    url = "https://swapi.tech/api/planets/2",
                    created = "2014-12-10T11:35:48.479000Z",
                    edited = "2014-12-20T20:58:18.420000Z"
                )
            )
        ),
        PlanetDetailResponseDto(
            message = "ok",
            result = PlanetResultDto(
                uid = "3",
                properties = PlanetDto(
                    name = "Hoth",
                    rotationPeriod = "23",
                    orbitalPeriod = "549",
                    diameter = "7200",
                    climate = "frozen",
                    gravity = "1.1 standard",
                    terrain = "tundra, ice caves, mountain ranges",
                    surfaceWater = "100",
                    population = "unknown",
                    url = "https://swapi.tech/api/planets/3",
                    created = "2014-12-10T11:39:13.934000Z",
                    edited = "2014-12-20T20:58:18.423000Z"
                )
            )
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
    fun `WHEN Planets are fetched from the remote THEN result holds correct data`() = runTest {
        coEvery { apiService.getPlanets() } returns Response.success(fakePlanetsResponse)
        coEvery { apiService.getPlanetDetail(any()) } returnsMany listOf<Response<PlanetDetailResponseDto>>(
            Response.success(fakePlanetDetails[0]),
            Response.success(fakePlanetDetails[1]),
            Response.success(fakePlanetDetails[2]),
        )

        val networkSource = PlanetNetworkDataSource(apiService)
        val planets = networkSource.getPlanets().getOrNull()

        for (i in 0..2) {
            assertEquals(fakePlanetDetails[i].result.properties.name, planets?.get(i)?.name)
        }
    }

    @Test
    fun `WHEN Planets fetch errors THEN throws exception`() = runTest {
        coEvery { apiService.getPlanets() } returns Response.error(404, "".toResponseBody(null))

        val networkSource = PlanetNetworkDataSource(apiService)

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                networkSource.getPlanets().getOrThrow()
            }
        }
    }
}