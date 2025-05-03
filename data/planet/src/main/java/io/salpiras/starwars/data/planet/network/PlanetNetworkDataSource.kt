package io.salpiras.starwars.data.planet.network

import io.salpiras.starwars.data.planet.network.dto.PlanetDto
import io.salpiras.starwars.data.planet.network.dto.PlanetsResponseDto
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class PlanetNetworkDataSource @Inject constructor(private val api: PlanetApiService) {

    suspend fun getPlanets(page: Int = 1): Result<List<PlanetDto>> =
        safeApiCall {
            // 1) fetch the page of summaries
            val pageDto: PlanetsResponseDto = api
                .getPlanets(page)
                .unwrap()

            // 2) for each summary, fetch detail and pull out .properties (PlanetDto)
            pageDto.results.map { summary ->
                api.getPlanetDetail(summary.uid)
                    .unwrap()
                    .result
                    .properties
            }
        }

    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> =
        runCatching { apiCall() }
            .recoverCatching { throwable ->
                if (throwable is CancellationException) throw throwable
                throw IllegalStateException("Network call failed", throwable)
            }

    private fun <T> Response<T>.unwrap(): T {
        if (this.isSuccessful) {
            return this.body() ?: throw IllegalStateException("Empty response")
        } else {
            throw IllegalStateException("Error while fetching data. Code: ${this.code()}")
        }
    }
}