package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.collection.SycnCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val sycnCollection: SycnCollection
) : ViewModel() {

    fun onCreated() {
    }

    fun sync() {
        viewModelScope.launch {
            sycnCollection.invoke()
        }
    }


}