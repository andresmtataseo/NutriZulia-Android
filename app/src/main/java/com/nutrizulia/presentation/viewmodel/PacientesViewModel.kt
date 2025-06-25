package com.nutrizulia.presentation.viewmodel

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.nutrizulia.domain.model.Paciente
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.FlowPreview
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.debounce
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.flow.stateIn
//import javax.inject.Inject
//
//@HiltViewModel
//class PacientesViewModel @Inject constructor(
//    private val getPacientesByFiltro: GetPacientesByFiltro
//) : ViewModel() {
//
//    private val _filtro = MutableStateFlow("")
//    val filtro: StateFlow<String> = _filtro
//
//    private val _mensaje = MutableStateFlow<String?>(null)
//    val mensaje: StateFlow<String?> = _mensaje
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    // Lista de todos los pacientes
//    val pacientes: StateFlow<List<Paciente>> = getPacientesByFiltro("")
//        .onEach {
//            _isLoading.value = false
//            if (it.isEmpty()) {
//                _mensaje.value = "No se encontraron pacientes."
//            }
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = emptyList()
//        )
//
//    // Lista de pacientes filtrados
//    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
//    val pacientesFiltrados: StateFlow<List<Paciente>> = _filtro
//        .debounce(300)
//        .distinctUntilChanged()
//        .flatMapLatest { query ->
//            _isLoading.value = true
//            getPacientesByFiltro(query)
//        }
//        .onEach {
//            _isLoading.value = false
//            if (it.isEmpty()) {
//                _mensaje.value = "No se encontraron pacientes."
//            }
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = emptyList()
//        )
//
//    // Función para actualizar el filtro de búsqueda
//    fun buscarPacientes(query: String) {
//        _mensaje.value = null
//        _filtro.value = query.trim()
//    }
//}
