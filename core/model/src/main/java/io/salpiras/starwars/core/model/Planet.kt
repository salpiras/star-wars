package io.salpiras.starwars.core.model

data class Planet(
    val uid: String,
    val name: String,
    val population: Long?,
    val climate: Set<Climate>,
    val diameter: Long?,
    val gravity: String, // string for now, but it's usually a float + standard or surface + additional info possibly
    val terrain: Set<Terrain>
)