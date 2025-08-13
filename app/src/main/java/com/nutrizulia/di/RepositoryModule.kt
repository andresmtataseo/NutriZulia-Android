package com.nutrizulia.di

import com.nutrizulia.data.local.dao.PacienteConCitaDao
import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.data.repository.collection.DetalleAntropometricoRepository
import com.nutrizulia.data.repository.collection.DetalleMetabolicoRepository
import com.nutrizulia.data.repository.collection.DetalleObstetriciaRepository
import com.nutrizulia.data.repository.collection.DetallePediatricoRepository
import com.nutrizulia.data.repository.collection.DetalleVitalRepository
import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.domain.usecase.SyncCollectionBatch
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePacienteRepository(
        pacienteDao: PacienteDao,
        pacienteConCitaDao: PacienteConCitaDao,
        batchSyncService: IBatchSyncService
    ): PacienteRepository {
        return PacienteRepository(pacienteDao, pacienteConCitaDao, batchSyncService)
    }

    @Singleton
    @Provides
    fun provideSyncCollectionBatch(
        pacienteRepository: PacienteRepository,
        consultaRepository: ConsultaRepository,
        antropometricoRepository: DetalleAntropometricoRepository,
        metabolicoRepository: DetalleMetabolicoRepository,
        obstetriciaRepository: DetalleObstetriciaRepository,
        pediatricoRepository: DetallePediatricoRepository,
        vitalRepository: DetalleVitalRepository,
        diagnosticoRepository: DiagnosticoRepository,
        evaluacionRepository: EvaluacionAntropometricaRepository,
        pacienteRepresentanteRepository: PacienteRepresentanteRepository,
        representanteRepository: RepresentanteRepository
    ): SyncCollectionBatch {
        return SyncCollectionBatch(
            pacienteRepository = pacienteRepository,
            consultaRepository = consultaRepository,
            antropometricoRepository = antropometricoRepository,
            metabolicoRepository = metabolicoRepository,
            obstetriciaRepository = obstetriciaRepository,
            pediatricoRepository = pediatricoRepository,
            vitalRepository = vitalRepository,
            diagnosticoRepository = diagnosticoRepository,
            evaluacionRepository = evaluacionRepository,
            pacienteRepresentanteRepository = pacienteRepresentanteRepository,
            representanteRepository = representanteRepository
        )
    }
}