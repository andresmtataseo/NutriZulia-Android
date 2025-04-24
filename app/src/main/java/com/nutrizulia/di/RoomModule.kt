package com.nutrizulia.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.nutrizulia.data.local.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val DATABASE_NAME = "nutrizulia_database"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun providePacienteDao(database: Database) = database.getPacienteDao()

    @Singleton
    @Provides
    fun provideEntidadDao(database: Database) = database.getEntidadDao()

    @Singleton
    @Provides
    fun provideMunicipioDao(database: Database) = database.getMunicipioDao()

    @Singleton
    @Provides
    fun provideParroquiaDao(database: Database) = database.getParroquiaDao()

    @Singleton
    @Provides
    fun provideComunidadDao(database: Database) = database.getComunidadDao()

    @Singleton
    @Provides
    fun provideRepresentanteDao(database: Database) = database.getRepresentanteDao()

    @Singleton
    @Provides
    fun provideCitaDao(database: Database) = database.getCitaDao()

    @Singleton
    @Provides
    fun provideConsultaDao(database: Database) = database.getConsultaDao()

    @Singleton
    @Provides
    fun provideSignosVitalesDao(database: Database) = database.getSignosVitalesDao()

    @Singleton
    @Provides
    fun provideUsuarioDao(database: Database) = database.getUsuarioDao()

    @Singleton
    @Provides
    fun provideActividadDao(database: Database) = database.getActividadDao()

}