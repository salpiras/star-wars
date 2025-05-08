package io.salpiras.starwars.core.model

enum class Terrain {
    DESERT,
    GRASSLANDS,
    MOUNTAINS,
    JUNGLE,
    RAINFORESTS,
    TUNDRA,
    ICE_CAVES,
    MOUNTAIN_RANGES,
    SWAMP,
    JUNGLES,
    GAS_GIANT,
    FORESTS,
    LAKES,
    GRASSY_HILLS,
    CITYSCAPE,
    OCEANS,
    ROCKY_DESERTS,
    CANYONS,
    SEAS,
    SCRUBLANDS,
    SINKHOLES,
    VOLCANOES,
    LAVA_RIVERS,
    CAVES,
    UNKNOWN;  // for any unrecognized or new values

    companion object {
        /**
         * Parse a raw API terrain string (e.g. "grasslands, mountains")
         * into the corresponding set of Terrain enums.
         */
        fun parseAll(raw: String): Set<Terrain> =
            raw.split(',')
                .map { it.trim().replace(' ', '_').uppercase() }
                .mapNotNull { token ->
                    entries.find { it.name == token }
                }
                .ifEmpty { setOf(UNKNOWN) }.toSet()
    }
}