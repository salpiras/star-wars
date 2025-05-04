package io.salpiras.starwars.core.domain.usecase

import io.salpiras.starwars.core.domain.repository.PlanetRepository
import io.salpiras.starwars.core.model.Planet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlanetsUseCase @Inject constructor(private val repository: PlanetRepository) {

    operator fun invoke(): Flow<List<Planet>> = repository.observePlanets()
}