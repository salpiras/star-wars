package io.salpiras.starwars.data.planet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.salpiras.starwars.data.planet.network.PlanetApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    internal fun providePlanetsApi(retrofit: Retrofit): PlanetApiService =
        retrofit.create(PlanetApiService::class.java)
}