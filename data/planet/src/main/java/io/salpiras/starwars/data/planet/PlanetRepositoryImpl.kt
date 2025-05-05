package io.salpiras.starwars.data.planet

import android.util.Log
import io.salpiras.starwars.core.domain.repository.PlanetRepository
import io.salpiras.starwars.core.model.OpResult
import io.salpiras.starwars.core.model.Planet
import io.salpiras.starwars.data.planet.network.PlanetNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO: inject dispatcher
// TODO: local source?
class PlanetRepositoryImpl @Inject constructor(val networkDataSource: PlanetNetworkDataSource) :
    PlanetRepository {
    private val planets = MutableStateFlow<List<Planet>>(emptyList())

    override fun observePlanets(): Flow<List<Planet>> = planets

    override fun observePlanet(planetId: String): Flow<Planet> =
        planets.mapNotNull { list -> list.firstOrNull { it.uid == planetId } }
            .distinctUntilChanged()

    override suspend fun refreshPlanets(page: Int): OpResult = withContext(Dispatchers.IO) {
        networkDataSource.getPlanets().fold(
            onSuccess = { planetsNetwork ->
                planets.value = planetsNetwork
                OpResult.Success
            },
            onFailure = {
                it.printStackTrace()
                Log.d("Network", "Error! $it")
                OpResult.Error
            }
        )
    }


}