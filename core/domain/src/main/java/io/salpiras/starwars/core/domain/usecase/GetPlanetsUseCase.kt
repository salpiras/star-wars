package io.salpiras.starwars.core.domain.usecase

import io.salpiras.starwars.core.domain.repository.PlanetRepository
import io.salpiras.starwars.core.model.OpResult
import javax.inject.Inject

class GetPlanetsUseCase @Inject constructor(private val repository: PlanetRepository) {
    suspend operator fun invoke(): OpResult = repository.refreshPlanets()
}