package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.ui.EventoHistoriaClinica
import com.nutrizulia.domain.model.ui.TipoEvento
import com.nutrizulia.domain.model.ui.CategoriaDetalle
import com.nutrizulia.domain.exception.HistorialPacienteException
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.usecase.historial.GetHistorialCompletoUseCase
import com.nutrizulia.domain.usecase.historial.GetHistorialPaginadoUseCase
import com.nutrizulia.domain.usecase.historial.GetEspecialidadesPacienteUseCase
import com.nutrizulia.domain.usecase.historial.GetAniosHistorialPacienteUseCase
import com.nutrizulia.util.SessionManager
import com.nutrizulia.domain.mapper.HistorialMedicoMapper
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CancellationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import android.util.Log

data class FiltroHistorial(
    val tipoEvento: TipoEvento? = null,
    val especialidad: String? = null,
    val fechaInicio: LocalDate? = null,
    val fechaFin: LocalDate? = null,
    val textoBusqueda: String? = null,
    val ano: Int? = null,
    val categoriaDetalle: CategoriaDetalle? = null
)

data class EstadoHistorial(
    val eventos: List<EventoHistoriaClinica> = emptyList(),
    val filtro: FiltroHistorial = FiltroHistorial(),
    val especialidades: List<String> = emptyList(),
    val anos: List<Int> = emptyList(),
    val paginaActual: Int = 0,
    val hayMasPaginas: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoriaPacienteViewModel @Inject constructor(
    private val pacienteRepository: PacienteRepository,
    private val getHistorialCompletoUseCase: GetHistorialCompletoUseCase,
    private val getHistorialPaginadoUseCase: GetHistorialPaginadoUseCase,
    private val getEspecialidadesPacienteUseCase: GetEspecialidadesPacienteUseCase,
    private val getAniosHistorialPacienteUseCase: GetAniosHistorialPacienteUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "HistoriaPacienteViewModel"
        private const val ITEMS_PER_PAGE = 20
    }

    // Estado del paciente
    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente

    private val _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> = _idUsuarioInstitucion

    // Estado del historial
    private val _estadoHistorial = MutableLiveData<EstadoHistorial>()
    val estadoHistorial: LiveData<EstadoHistorial> = _estadoHistorial

    // Estados de UI
    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> = _mensaje

    private val _eventoSeleccionado = MutableLiveData<EventoHistoriaClinica?>()
    val eventoSeleccionado: LiveData<EventoHistoriaClinica?> = _eventoSeleccionado

    init {
        _estadoHistorial.value = EstadoHistorial()
        loadCurrentInstitutionId()
    }

    private fun loadCurrentInstitutionId() {
        viewModelScope.launch {
            try {
                val institutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
                _idUsuarioInstitucion.value = institutionId!!
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener la institución actual", e)
                val errorMessage = when (e) {
                    is UnknownHostException, is SocketTimeoutException -> "Error de conexión. Verifique su conexión a internet."
                    is SSLException -> "Error de seguridad en la conexión."
                    else -> "Error al obtener la institución actual: ${e.message}"
                }
                _mensaje.value = errorMessage
                updateEstado { it.copy(error = errorMessage) }
            }
        }
    }

    fun inicializarHistorial(pacienteId: String) {
        viewModelScope.launch {
            try {
                updateEstado { it.copy(isLoading = true, error = null) }
                
                // Cargar paciente
                val usuarioInstitucionId = sessionManager.currentInstitutionIdFlow.firstOrNull() ?: 1
                val paciente = pacienteRepository.findById(usuarioInstitucionId, pacienteId)
                    ?: throw HistorialPacienteException.PacienteNoEncontradoException(pacienteId)
                _paciente.value = paciente
                
                // Cargar datos iniciales del historial
                loadHistorialInicial(pacienteId)
                loadEspecialidades(pacienteId)
                loadAnos(pacienteId)
                
            } catch (e: HistorialPacienteException) {
                Log.e(TAG, "Error específico del historial: ${e.message}", e)
                val errorMessage = e.message ?: "Error desconocido"
                updateEstado { it.copy(isLoading = false, error = errorMessage) }
                _mensaje.value = errorMessage
            } catch (e: UnknownHostException) {
                Log.e(TAG, "Error de conexión al inicializar historial", e)
                val errorMessage = "Sin conexión a internet. Verifique su conexión."
                updateEstado { it.copy(isLoading = false, error = errorMessage) }
                _mensaje.value = errorMessage
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Timeout al inicializar historial", e)
                val errorMessage = "La conexión tardó demasiado. Intente nuevamente."
                updateEstado { it.copy(isLoading = false, error = errorMessage) }
                _mensaje.value = errorMessage
            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado al inicializar historial", e)
                val errorMessage = "Error inesperado: ${e.message}"
                updateEstado { it.copy(isLoading = false, error = errorMessage) }
                _mensaje.value = errorMessage
            }
        }
    }

    private suspend fun loadHistorialInicial(pacienteId: String) {
        try {
            val historialCompleto = getHistorialCompletoUseCase(pacienteId).first()
            val eventos = historialCompleto.map { historial ->
                HistorialMedicoMapper.mapToEventoHistoriaClinica(historial)
            }.sortedByDescending { it.fechaEvento }
            
            updateEstado { 
                it.copy(
                    eventos = eventos,
                    paginaActual = 0,
                    hayMasPaginas = eventos.size >= ITEMS_PER_PAGE,
                    isLoading = false
                )
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Carga de historial inicial cancelada (fragmento cerrado)", e)
            // No lanzar excepción para cancelaciones normales del ciclo de vida
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Error de conexión al cargar historial inicial", e)
            throw HistorialPacienteException.ConexionException("Sin conexión a internet", e)
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Timeout al cargar historial inicial", e)
            throw HistorialPacienteException.ConexionException("La conexión tardó demasiado", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar historial inicial", e)
            throw HistorialPacienteException.HistorialNoDisponibleException(pacienteId, e)
        }
    }

    private suspend fun loadEspecialidades(pacienteId: String) {
        try {
            val especialidades = getEspecialidadesPacienteUseCase(pacienteId)
            updateEstado { it.copy(especialidades = especialidades) }
        } catch (e: UnknownHostException) {
            Log.w(TAG, "Error de conexión al cargar especialidades", e)
            // No lanzamos excepción para especialidades ya que no es crítico
        } catch (e: SocketTimeoutException) {
            Log.w(TAG, "Timeout al cargar especialidades", e)
            // No lanzamos excepción para especialidades ya que no es crítico
        } catch (e: Exception) {
            Log.w(TAG, "Error al cargar especialidades", e)
            // No lanzamos excepción para especialidades ya que no es crítico
        }
    }

    private suspend fun loadAnos(pacienteId: String) {
        try {
            val anos = getAniosHistorialPacienteUseCase(pacienteId)
            updateEstado { it.copy(anos = anos) }
        } catch (e: UnknownHostException) {
            Log.w(TAG, "Error de conexión al cargar años del historial", e)
            // No lanzamos excepción para años ya que no es crítico
        } catch (e: SocketTimeoutException) {
            Log.w(TAG, "Timeout al cargar años del historial", e)
            // No lanzamos excepción para años ya que no es crítico
        } catch (e: Exception) {
            Log.w(TAG, "Error al cargar años del historial", e)
            // No lanzamos excepción para años ya que no es crítico
        }
    }

    fun aplicarFiltro(nuevoFiltro: FiltroHistorial) {
        val pacienteId = _paciente.value?.id ?: return
        
        viewModelScope.launch {
            try {
                updateEstado { it.copy(isLoading = true, filtro = nuevoFiltro, paginaActual = 0) }
                
                val historialViews = getHistorialPaginadoUseCase(
                    pacienteId = pacienteId,
                    pagina = 0,
                    tamanoPagina = ITEMS_PER_PAGE
                )
                
                val eventos = historialViews.map { view ->
                    EventoHistoriaClinica(
                        consultaId = view.consultaId,
                        pacienteId = view.pacienteId,
                        fechaEvento = view.fechaConsulta,
                        tipoEvento = TipoEvento.CONSULTA_GENERAL,
                        titulo = "Consulta - ${view.especialidadNombre ?: "General"}",
                        descripcion = view.motivoConsulta ?: "Sin descripción",
                        estado = view.estadoConsulta,
                        profesional = "${view.pacienteNombres} ${view.pacienteApellidos}",
                        especialidad = view.especialidadNombre,
                        ultimaActualizacion = view.ultimaActualizacion
                    )
                }.filter { evento ->
                    aplicarFiltrosEvento(evento, nuevoFiltro)
                }.sortedByDescending { it.fechaEvento }
                
                updateEstado { 
                    it.copy(
                        eventos = eventos,
                        hayMasPaginas = eventos.size == ITEMS_PER_PAGE,
                        isLoading = false
                    )
                }
                
            } catch (e: Exception) {
                Log.e("HistoriaPacienteViewModel", "Error al aplicar filtro", e)
                updateEstado { it.copy(isLoading = false, error = "Error al aplicar filtro: ${e.message}") }
                _mensaje.value = "Error al aplicar filtro: ${e.message}"
            }
        }
    }

    fun cargarMasPaginas() {
        val estado = _estadoHistorial.value ?: return
        if (!estado.hayMasPaginas || estado.isLoadingMore) return
        
        val pacienteId = _paciente.value?.id ?: return
        
        viewModelScope.launch {
            try {
                updateEstado { it.copy(isLoadingMore = true) }
                
                val nuevaPagina = estado.paginaActual + 1
                val offset = nuevaPagina * ITEMS_PER_PAGE
                
                val nuevosEventos = if (esFiltroBusqueda(estado.filtro)) {
                    // Para búsqueda no hay paginación adicional
                    emptyList()
                } else {
                    val historialViews = getHistorialPaginadoUseCase(
                        pacienteId = pacienteId,
                        pagina = nuevaPagina,
                        tamanoPagina = ITEMS_PER_PAGE
                    )
                    // Convertir HistorialMedicoCompletoView a EventoHistoriaClinica
                    historialViews.map { view ->
                        EventoHistoriaClinica(
                            consultaId = view.consultaId,
                            pacienteId = view.pacienteId,
                            fechaEvento = view.fechaConsulta,
                            tipoEvento = TipoEvento.CONSULTA_GENERAL,
                            titulo = "Consulta - ${view.especialidadNombre ?: "General"}",
                            descripcion = view.motivoConsulta ?: "Sin descripción",
                            estado = view.estadoConsulta,
                            profesional = "${view.pacienteNombres} ${view.pacienteApellidos}",
                            especialidad = view.especialidadNombre,
                            ultimaActualizacion = view.ultimaActualizacion
                        )
                    }.sortedByDescending { it.fechaEvento }
                }
                
                updateEstado { 
                    it.copy(
                        eventos = it.eventos + nuevosEventos,
                        paginaActual = nuevaPagina,
                        hayMasPaginas = nuevosEventos.size == ITEMS_PER_PAGE,
                        isLoadingMore = false
                    )
                }
                
            } catch (e: Exception) {
                Log.e("HistoriaPacienteViewModel", "Error al cargar más páginas", e)
                updateEstado { it.copy(isLoadingMore = false, error = "Error al cargar más eventos: ${e.message}") }
                _mensaje.value = "Error al cargar más eventos: ${e.message}"
            }
        }
    }

    fun limpiarFiltros() {
        aplicarFiltro(FiltroHistorial())
    }

    fun seleccionarEvento(evento: EventoHistoriaClinica) {
        _eventoSeleccionado.value = evento
    }

    fun limpiarEventoSeleccionado() {
        _eventoSeleccionado.value = null
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun refrescarHistorial() {
        val pacienteId = _paciente.value?.id ?: return
        inicializarHistorial(pacienteId)
    }

    private fun esFiltroBusqueda(filtro: FiltroHistorial): Boolean {
        return !filtro.textoBusqueda.isNullOrBlank()
    }

    private fun updateEstado(update: (EstadoHistorial) -> EstadoHistorial) {
        _estadoHistorial.value = update(_estadoHistorial.value ?: EstadoHistorial())
    }

    // Métodos de conveniencia para filtros específicos
    fun filtrarPorTipoEvento(tipoEvento: TipoEvento?) {
        val filtroActual = _estadoHistorial.value?.filtro ?: FiltroHistorial()
        aplicarFiltro(filtroActual.copy(tipoEvento = tipoEvento))
    }

    fun filtrarPorEspecialidad(especialidad: String?) {
        val filtroActual = _estadoHistorial.value?.filtro ?: FiltroHistorial()
        aplicarFiltro(filtroActual.copy(especialidad = especialidad))
    }

    fun filtrarPorRangoFechas(fechaInicio: LocalDate?, fechaFin: LocalDate?) {
        val filtroActual = _estadoHistorial.value?.filtro ?: FiltroHistorial()
        aplicarFiltro(filtroActual.copy(fechaInicio = fechaInicio, fechaFin = fechaFin))
    }

    fun filtrarPorAno(ano: Int?) {
        val filtroActual = _estadoHistorial.value?.filtro ?: FiltroHistorial()
        aplicarFiltro(filtroActual.copy(ano = ano))
    }

    fun buscarTexto(texto: String?) {
        val filtroActual = _estadoHistorial.value?.filtro ?: FiltroHistorial()
        aplicarFiltro(filtroActual.copy(textoBusqueda = texto))
    }
    
    fun filtrarPorCategoriaDetalle(categoria: CategoriaDetalle?) {
        val filtroActual = _estadoHistorial.value?.filtro ?: FiltroHistorial()
        aplicarFiltro(filtroActual.copy(categoriaDetalle = categoria))
    }
    
    private fun aplicarFiltrosEvento(evento: EventoHistoriaClinica, filtro: FiltroHistorial): Boolean {
        // Filtro por tipo de evento
        if (filtro.tipoEvento != null && evento.tipoEvento != filtro.tipoEvento) {
            return false
        }
        
        // Filtro por especialidad
        if (filtro.especialidad != null && evento.especialidad != filtro.especialidad) {
            return false
        }
        
        // Filtro por año
        if (filtro.ano != null && evento.fechaEvento?.year != filtro.ano) {
            return false
        }
        
        // Filtro por rango de fechas
        if (filtro.fechaInicio != null && evento.fechaEvento?.toLocalDate()?.isBefore(filtro.fechaInicio) == true) {
            return false
        }
        if (filtro.fechaFin != null && evento.fechaEvento?.toLocalDate()?.isAfter(filtro.fechaFin) == true) {
            return false
        }
        
        // Filtro por categoría de detalle
        if (filtro.categoriaDetalle != null) {
            val tieneCategoria = when (filtro.categoriaDetalle) {
                CategoriaDetalle.DIAGNOSTICO -> evento.tieneDiagnosticos()
                CategoriaDetalle.ANTROPOMETRICO -> evento.tieneDetallesAntropometricos()
                CategoriaDetalle.VITAL -> evento.tieneDetallesVitales()
                CategoriaDetalle.METABOLICO -> evento.tieneDetallesMetabolicos()
                CategoriaDetalle.PEDIATRICO -> evento.tieneDetallesPediatricos()
                CategoriaDetalle.OBSTETRICO -> evento.tieneDetallesObstetricos()
                CategoriaDetalle.EVALUACION -> evento.tieneEvaluaciones()
            }
            if (!tieneCategoria) {
                return false
            }
        }
        
        // Filtro por texto de búsqueda
        if (!filtro.textoBusqueda.isNullOrBlank()) {
            val textoBusqueda = filtro.textoBusqueda.lowercase()
            val coincide = evento.titulo.lowercase().contains(textoBusqueda) ||
                          evento.descripcion?.lowercase()?.contains(textoBusqueda) == true ||
                          evento.especialidad?.lowercase()?.contains(textoBusqueda) == true ||
                          evento.profesional?.lowercase()?.contains(textoBusqueda) == true
            if (!coincide) {
                return false
            }
        }
        
        return true
    }
}