package com.nutrizulia.presentation.view.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.catalog.Parentesco
import com.nutrizulia.domain.usecase.catalog.GetParentescos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeleccionarParentescoDialogViewModel @Inject constructor(
    private val getParentescos: GetParentescos
) : ViewModel() {

    private val _parentescos = MutableLiveData<List<Parentesco>>()
    val parentescos: LiveData<List<Parentesco>> get() = _parentescos
    private val _selectedParentesco = MutableLiveData<Parentesco?>()
    val selectedParentesco: LiveData<Parentesco?> = _selectedParentesco

    fun onParentescoSelected(parentesco: Parentesco) { _selectedParentesco.value = parentesco }

    fun obtenerParentescos() {
        viewModelScope.launch {
            val parentescosResult = getParentescos()
            if (parentescosResult.isNotEmpty()) {
                _parentescos.value = parentescosResult
            }
        }
    }

}