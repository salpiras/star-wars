package io.salpiras.starwars.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://swapi.tech/"
    private const val CONTENT_TYPE = "application/json"

    @Singleton
    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(converterFactory)
            .client(httpClient)
            .build()

    @Singleton
    @Provides
    internal fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().build()

    @Singleton
    @Provides
    internal fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    internal fun provideConverterFactory(json: Json): Converter.Factory =
        json.asConverterFactory(CONTENT_TYPE.toMediaType())
}