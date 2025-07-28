package com.nutrizulia.presentation.viewmodel.representante

import androidx.lifecycle.ViewModel
import com.nutrizulia.domain.usecase.collection.GetRepresentanteById
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccionesRepresentanteViewModel @Inject constructor(
    private val getRepresentanteById: GetRepresentanteById,
): ViewModel() {

}