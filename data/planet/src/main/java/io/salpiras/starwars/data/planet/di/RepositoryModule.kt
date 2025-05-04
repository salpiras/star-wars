package io.salpiras.starwars.data.planet.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.salpiras.starwars.core.domain.repository.PlanetRepository
import io.salpiras.starwars.data.planet.PlanetRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun provideRepository(repo: PlanetRepositoryImpl): PlanetRepository
}