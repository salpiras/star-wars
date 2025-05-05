package io.salpiras.starwars.core.domain.repository

import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.Flow

interface PlanetRepository {

    fun observePlanet(planetId: String): Flow<Planet>

    fun observePlanets(): Flow<List<Planet>>

    suspend fun refreshPlanets(page: Int = 1): OpResult

}