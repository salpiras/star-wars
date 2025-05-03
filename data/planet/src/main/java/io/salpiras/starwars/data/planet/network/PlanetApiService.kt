package io.salpiras.starwars.data.planet.network

import io.salpiras.starwars.data.planet.network.dto.PlanetDetailResponseDto
import io.salpiras.starwars.data.planet.network.dto.PlanetsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanetApiService {

    @GET("api/planets")
    suspend fun getPlanets(
        @Query("page") page: Int = 1
    ): Response<PlanetsResponseDto>

    @GET("api/planets/{id}")
    suspend fun getPlanetDetail(
        @Path("id") id: String
    ): Response<PlanetDetailResponseDto>
}