package io.salpiras.starwars.core.model

enum class Climate {
    ARID,
    TEMPERATE,
    TROPICAL,
    FROZEN,
    MURKY,
    WINDY,
    HOT,
    POLLUTED,
    HUMID,
    MOIST,
    FRIGID,
    ARTIFICIAL_TEMPERATE,
    UNKNOWN;  // API uses “artificial temperate”

    companion object {
        /**
         * Parse a raw API climate string (e.g. “temperate, tropical”)
         * into a set of Climate values.
         */
        // TODO: this might be just done by the mapper data -> model.
        fun parseAll(raw: String): Set<Climate> =
            raw.split(',')
                .map { it.trim().replace(' ', '_') }
                .mapNotNull { key ->
                    entries.find { it.name.equals(key, ignoreCase = true) }
                }.ifEmpty { setOf(UNKNOWN) }
                .toSet()
    }
}