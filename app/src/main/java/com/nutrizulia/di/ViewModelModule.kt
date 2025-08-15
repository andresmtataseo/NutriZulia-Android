package com.nutrizulia.di

import com.nutrizulia.domain.usecase.SyncCollectionBatch
import com.nutrizulia.domain.usecase.dashboard.GetPendingRecordsByEntityUseCase
import com.nutrizulia.presentation.viewmodel.SyncBatchViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun provideSyncBatchViewModel(
        syncCollectionBatch: SyncCollectionBatch,
        getPendingRecordsByEntityUseCase: GetPendingRecordsByEntityUseCase
    ): SyncBatchViewModel {
        return SyncBatchViewModel(syncCollectionBatch, getPendingRecordsByEntityUseCase)
    }
}