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

    private const val DATABASE_NAME = "nutrizulia_db"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        Log.d("RoomDB", "Ruta de la base de datos: ${context.getDatabasePath("nutrizulia.db")}")
        return Room.databaseBuilder(
            context,
            Database::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun providePacienteDao(database: Database) = database.getPacienteDao()

}