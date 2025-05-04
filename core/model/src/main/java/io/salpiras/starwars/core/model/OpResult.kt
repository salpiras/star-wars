package io.salpiras.starwars.core.model

sealed interface OpResult {
    data object Success : OpResult
    data object Error : OpResult
}