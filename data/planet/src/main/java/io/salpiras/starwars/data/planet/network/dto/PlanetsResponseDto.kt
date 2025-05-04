package io.salpiras.starwars.data.planet.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanetsResponseDto(
    val message: String,
    @SerialName("total_records") val totalRecords: Int,
    @SerialName("total_pages")   val totalPages: Int,
    val previous: String? = null,
    val next:     String? = null,
    val results:  List<PlanetSummaryDto>
)

@Serializable
data class PlanetSummaryDto(
    val uid:  String,
    val name: String,
    val url:  String,
)