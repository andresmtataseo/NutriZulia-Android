package com.nutrizulia.presentation.viewmodel.consulta

import android.util.Log
import androidx.lifecycle.*
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.*
import com.nutrizulia.domain.usecase.collection.*
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ConsultaSharedViewModel @Inject constructor(
    private val getPaciente: GetPacienteById,
    private val getConsulta: GetConsultaProgramadaById,
    private val saveConsulta: SaveConsulta,
    private val saveDetalleVital: SaveDetalleVital,
    private val saveDetalleAntropometrico: SaveDetalleAntropometrico,
    private val saveDetalleMetabolico: SaveDetalleMetabolico,
    private val saveDetalleObstetricia: SaveDetalleObstetricia,
    private val saveDetallePediatrico: SaveDetallePediatrico,
    private val saveDiagnosticos: SaveDiagnosticos,
    private val saveEvaluacionesAntropometricas: SaveEvaluacionesAntropometricas,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
) : ViewModel() {

    // MARK: - Shared State
    private var isInitialized: Boolean = false

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente

    private val _consulta = MutableLiveData<Consulta>()
    val consulta: LiveData<Consulta> = _consulta

    private val _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> = _idUsuarioInstitucion

    private val _modoConsulta = MutableLiveData<ModoConsulta>()
    val modoConsulta: LiveData<ModoConsulta> = _modoConsulta

    // Variable para mantener el estado de isHistoria a través de todo el flujo
    private val _isHistoria = MutableLiveData<Boolean>()
    val isHistoria: LiveData<Boolean> = _isHistoria
    
    // Método para establecer el valor de isHistoria
    fun setIsHistoria(value: Boolean) {
        Log.d("NavFlow", "ConsultaSharedViewModel: setIsHistoria($value)")
        _isHistoria.value = value
    }
    
    // Método para obtener el valor actual de isHistoria
    fun getIsHistoriaValue(): Boolean {
        return _isHistoria.value ?: false
    }

    private val _consultaEditando = MutableLiveData<Consulta>()
    val consultaEditando: LiveData<Consulta> = _consultaEditando

    private val _detalleVital = MutableLiveData<DetalleVital?>()
    val detalleVital: LiveData<DetalleVital?> = _detalleVital

    private val _detalleAntropometrico = MutableLiveData<DetalleAntropometrico?>()
    val detalleAntropometrico: LiveData<DetalleAntropometrico?> = _detalleAntropometrico

    private val _detalleMetabolico = MutableLiveData<DetalleMetabolico?>()
    val detalleMetabolico: LiveData<DetalleMetabolico?> = _detalleMetabolico

    private val _detallePediatrico = MutableLiveData<DetallePediatrico?>()
    val detallePediatrico: LiveData<DetallePediatrico?> = _detallePediatrico

    private val _detalleObstetricia = MutableLiveData<DetalleObstetricia?>()
    val detalleObstetricia: LiveData<DetalleObstetricia?> = _detalleObstetricia

    private val _diagnosticosSeleccionados = MutableLiveData<List<Diagnostico>>()
    val diagnosticosSeleccionados: LiveData<List<Diagnostico>> = _diagnosticosSeleccionados

    private val _evaluacionesAntropometricas = MutableLiveData<List<EvaluacionAntropometrica>>()
    val evaluacionesAntropometricas: LiveData<List<EvaluacionAntropometrica>> = _evaluacionesAntropometricas

    // MARK: - UI Control
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir

    // MARK: - Public Updaters
    fun updateConsultaParcial(tipoActividad: TipoActividad, especialidad: Especialidad, tipoConsulta: TipoConsulta, motivo: String?) {
        val idConsulta = consulta.value?.id ?: Utils.generarUUID()
        val idUsuarioInst = idUsuarioInstitucion.value ?: 0

        val nuevaConsulta = Consulta(
            id = idConsulta,
            usuarioInstitucionId = idUsuarioInst,
            pacienteId = paciente.value?.id ?: "",
            tipoActividadId = tipoActividad.id,
            especialidadRemitenteId = especialidad.id,
            tipoConsulta = tipoConsulta,
            motivoConsulta = if (motivo.isNullOrBlank()) null else motivo,
            fechaHoraProgramada = consulta.value?.fechaHoraProgramada,
            observaciones = consulta.value?.observaciones,
            planes = consulta.value?.planes,
            fechaHoraReal = consulta.value?.fechaHoraReal,
            estado = consulta.value?.estado ?: Estado.SIN_PREVIA_CITA,
            updatedAt = LocalDateTime.now(),
            isDeleted = false,
            isSynced = false
        )
        _consultaEditando.value = nuevaConsulta
    }

    fun updateDetalleVital(detalle: DetalleVital?) {
        _detalleVital.value = detalle
    }

    fun updateDetalleAntropometrico(detalle: DetalleAntropometrico?) {
        _detalleAntropometrico.value = detalle
    }

    fun updateDetalleMetabolico(detalle: DetalleMetabolico?) {
        _detalleMetabolico.value = detalle
    }

    fun updateDetallePediatrico(detalle: DetallePediatrico?) {
        _detallePediatrico.value = detalle
    }

    fun updateDetalleObstetricia(detalle: DetalleObstetricia?) {
        _detalleObstetricia.value = detalle
    }

    fun updateDiagnosticos(diagnosticos: List<Diagnostico>) {
        _diagnosticosSeleccionados.value = diagnosticos
    }

    fun updateEvaluacionesAntropometricas(evaluaciones: List<EvaluacionAntropometrica>) {
        _evaluacionesAntropometricas.value = evaluaciones
    }

    // MARK: - Core Logic
    fun initialize(idPaciente: String, idConsulta: String?, isEditable: Boolean) {
        if (isInitialized) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val institutionId = getCurrentInstitutionId() ?: run {
                    _mensaje.value = "No se ha seleccionado una institución."
                    _salir.value = true
                    return@launch
                }
                _idUsuarioInstitucion.value = institutionId

                val pacienteResult = async { getPaciente(institutionId, idPaciente) }.await() ?: run {
                    _mensaje.value = "No se encontró el paciente."
                    _salir.value = true
                    return@launch
                }
                _paciente.value = pacienteResult

                if (idConsulta != null) {
                    val consultaResult = withContext(Dispatchers.IO) { getConsulta(idConsulta) } ?: run {
                        _mensaje.value = "No se encontró la consulta."
                        _salir.value = true
                        return@launch
                    }
                    _consulta.value = consultaResult
                    _consultaEditando.value = consultaResult

                    _modoConsulta.value = determineModoConsulta(consultaResult, isEditable)
                } else {
                    _modoConsulta.value = ModoConsulta.CREAR_SIN_CITA
                }
                Log.w("ConsultaSharedViewModel", _modoConsulta.value.toString())
                isInitialized = true

            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos: ${e.localizedMessage ?: "desconocido"}"
                _salir.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun determineModoConsulta(consulta: Consulta, isEditable: Boolean): ModoConsulta {
        return when (consulta.estado) {
            Estado.COMPLETADA -> if (isEditable) ModoConsulta.EDITAR_CONSULTA else ModoConsulta.VER_CONSULTA
            Estado.PENDIENTE, Estado.REPROGRAMADA -> if (isEditable) ModoConsulta.CULMINAR_CITA else ModoConsulta.VER_CONSULTA
            else -> if (isEditable) ModoConsulta.EDITAR_CONSULTA else ModoConsulta.VER_CONSULTA
        }
    }

    fun saveCompleteConsultation(observaciones: String?, planes: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val consultaAGuardar = consultaEditando.value ?: run {
                    _mensaje.value = "Consulta no válida para guardar"
                    return@launch
                }

                val consultaActualizada = consultaAGuardar.copy(
                    estado = if (consultaAGuardar.estado != Estado.SIN_PREVIA_CITA) Estado.COMPLETADA else consultaAGuardar.estado,
                    observaciones = observaciones?.takeIf { it.isNotBlank() },
                    planes = planes?.takeIf { it.isNotBlank() },
                    fechaHoraReal = consultaAGuardar.fechaHoraReal ?: LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                saveConsulta(consultaActualizada)

                detalleVital.value?.let { saveDetalleVital(it) }
                detalleAntropometrico.value?.let { saveDetalleAntropometrico(it) }
                detalleMetabolico.value?.let { saveDetalleMetabolico(it) }
                detalleObstetricia.value?.let { saveDetalleObstetricia(it) }
                detallePediatrico.value?.let { saveDetallePediatrico(it) }

                evaluacionesAntropometricas.value?.let { saveEvaluacionesAntropometricas(consultaActualizada.id, it) }
                diagnosticosSeleccionados.value?.let { saveDiagnosticos(consultaActualizada.id, it) }

                _mensaje.value = "Consulta guardada correctamente"
                _salir.value = true
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar consulta: ${e.localizedMessage}"
                Log.e("ConsultaSharedViewModel", "Error al guardar consulta", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}