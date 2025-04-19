package com.nutrizulia.di

import com.nutrizulia.data.remote.api.UbicacionApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val API_SEGEN_URL = "https://apisegen.apn.gob.ve/api/v1/"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_SEGEN_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideUbicacionApiClient(retrofit: Retrofit): UbicacionApiClient {
        return retrofit.create(UbicacionApiClient::class.java)
    }
}