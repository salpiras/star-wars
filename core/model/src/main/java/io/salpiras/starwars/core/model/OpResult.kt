package io.salpiras.starwars.core.model

sealed interface OpResult {
    data object Success : OpResult
    data class Error(val message: String?, val isFatal: Boolean) : OpResult
}