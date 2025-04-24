package com.nutrizulia.di

import com.nutrizulia.data.remote.api.ComunidadApiClient
import com.nutrizulia.data.remote.api.EntidadApiClient
import com.nutrizulia.data.remote.api.LoginSegenApiClient
import com.nutrizulia.data.remote.api.MunicipioApiClient
import com.nutrizulia.data.remote.api.ParroquiaApiClient
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
    fun provideLoginSegenApiClient(retrofit: Retrofit): LoginSegenApiClient {
        return retrofit.create(LoginSegenApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideEntidadApiClient(retrofit: Retrofit): EntidadApiClient {
        return retrofit.create(EntidadApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideMunicipioApiClient(retrofit: Retrofit): MunicipioApiClient {
        return retrofit.create(MunicipioApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideParroquiaApiClient(retrofit: Retrofit): ParroquiaApiClient {
        return retrofit.create(ParroquiaApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideComunidadApiClient(retrofit: Retrofit): ComunidadApiClient {
        return retrofit.create(ComunidadApiClient::class.java)
    }

}