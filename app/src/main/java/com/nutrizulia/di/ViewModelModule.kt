package com.nutrizulia.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    // ViewModels are now automatically provided by Hilt using @HiltViewModel annotation
}