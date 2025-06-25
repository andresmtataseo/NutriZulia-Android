package com.nutrizulia.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.nutrizulia.data.remote.api.auth.AuthInterceptor
import com.nutrizulia.data.remote.api.auth.IAuthService
import com.nutrizulia.data.remote.api.catalog.ICatalogService
import com.nutrizulia.data.remote.api.collection.ICollectionService
import com.nutrizulia.data.remote.api.user.IUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val BASE_URL = "http://192.168.1.100:8080/"

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            // Soporte para LocalDate
            .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE))
            })
            .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
                LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
            })

            // Soporte para LocalDateTime
            .registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            })
            .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            })
            .create()
    }


    // --- Nuevo: Provee HttpLoggingInterceptor para depuración ---
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // --- Nuevo: OkHttpClient para Autenticación (no necesita token aún) ---
    @Provides
    @Singleton
    @AuthOkHttpClient // Calificador personalizado para diferenciar este OkHttpClient
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // --- Nuevo: OkHttpClient para Llamadas Autenticadas (con token) ---
    @Provides
    @Singleton
    @AuthenticatedOkHttpClient // Calificador personalizado para diferenciar este OkHttpClient
    fun provideAuthenticatedOkHttpClient(
        authInterceptor: AuthInterceptor, // Tu interceptor personalizado para añadir el JWT
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // ¡Aquí es donde se añade tu JWT!
            .addInterceptor(loggingInterceptor) // Incluye el logger para depuración
            .build()
    }

    // --- Actualizado: Provee Retrofit para el Servicio de Autenticación ---
    // Esta instancia de Retrofit usa el OkHttpClient *sin* el AuthInterceptor
    @Singleton
    @Provides
    @AuthRetrofit // Calificador personalizado para esta instancia de Retrofit
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient, // Inyecta el OkHttpClient específico para auth
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Usa el OkHttpClient sin el token de autenticación
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- Actualizado: Provee Retrofit para Servicios Autenticados ---
    // Esta instancia de Retrofit usa el OkHttpClient *con* el AuthInterceptor
    @Singleton
    @Provides
    @AuthenticatedRetrofit // Calificador personalizado para esta instancia de Retrofit
    fun provideAuthenticatedRetrofit(
        @AuthenticatedOkHttpClient okHttpClient: OkHttpClient, // Inyecta el OkHttpClient autenticado
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Usa el OkHttpClient con el token de autenticación
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- Actualizado: Provee IAuthService usando el AuthRetrofit ---
    @Singleton
    @Provides
    fun provideAuthService(@AuthRetrofit retrofit: Retrofit): IAuthService {
        return retrofit.create(IAuthService::class.java)
    }

    // --- Actualizado: Provee otros servicios usando el AuthenticatedRetrofit ---
    @Singleton
    @Provides
    fun provideCatalogService(@AuthenticatedRetrofit retrofit: Retrofit): ICatalogService {
        return retrofit.create(ICatalogService::class.java)
    }

    @Singleton
    @Provides
    fun provideCollectionService(@AuthenticatedRetrofit retrofit: Retrofit): ICollectionService {
        return retrofit.create(ICollectionService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserService(@AuthenticatedRetrofit retrofit: Retrofit): IUserService {
        return retrofit.create(IUserService::class.java)
    }

}

// --- Nuevos: Calificadores personalizados para diferenciar instancias de Retrofit y OkHttpClient ---
// Esto es crucial para que Hilt sepa qué instancia inyectar cuando hay múltiples proveedores.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedRetrofit