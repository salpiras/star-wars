package io.salpiras.starwars.core.model

data class Planet(
    val uid: String,
    val name: String,
    val population: String,
    val climate: Set<Climate>,
    val diameter: Diameter,
    val gravity: String, // string for now, but it's usually a float + standard or surface + additional info possibly
    val terrain: Set<Terrain> // enum of terrains?
)