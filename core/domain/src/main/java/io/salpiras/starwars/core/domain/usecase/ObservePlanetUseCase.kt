package io.salpiras.starwars.core.domain.usecase

import io.salpiras.starwars.core.domain.repository.PlanetRepository
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlanetUseCase @Inject constructor(private val repository: PlanetRepository) {

    operator fun invoke(planetId: String): Flow<Planet> = repository.observePlanet(planetId)
}