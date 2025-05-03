package io.salpiras.starwars.data.planet.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanetDetailResponseDto(
    val message: String,
    val result: PlanetResultDto
)

@Serializable
data class PlanetResultDto(
    val uid: String,
    val properties: PlanetDto
)

@Serializable
data class PlanetDto(
    val name: String,
    @SerialName("rotation_period") val rotationPeriod: String,
    @SerialName("orbital_period") val orbitalPeriod: String,
    val diameter: String,
    val climate: String,
    val gravity: String,
    val terrain: String,
    @SerialName("surface_water") val surfaceWater: String,
    val population: String,
    val url: String,
    val created: String,
    val edited: String
)