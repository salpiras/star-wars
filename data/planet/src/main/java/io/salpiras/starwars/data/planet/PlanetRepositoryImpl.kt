package io.salpiras.starwars.data.planet

import io.salpiras.starwars.core.domain.repository.PlanetRepository
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import io.salpiras.starwars.data.planet.network.PlanetNetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlanetRepositoryImpl @Inject constructor(val networkDataSource: PlanetNetworkDataSource) :
    PlanetRepository {

    override fun observePlanets(): Flow<List<Planet>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshPlanets(page: Int = 1): OpResult {
        TODO("Not yet implemented")
    }


}