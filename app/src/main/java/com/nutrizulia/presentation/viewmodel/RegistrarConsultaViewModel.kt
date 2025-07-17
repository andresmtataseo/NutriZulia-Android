package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.data.local.enum.TipoLactancia
import com.nutrizulia.domain.model.catalog.*
import com.nutrizulia.domain.model.collection.*
import com.nutrizulia.domain.usecase.catalog.*
import com.nutrizulia.domain.usecase.collection.*
import com.nutrizulia.util.ModoConsulta
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.Utils
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import com.nutrizulia.domain.usecase.collection.GetDiagnosticosByConsultaId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RegistrarConsultaViewModel @Inject constructor(
    private val getPaciente: GetPacienteById,
    private val getConsulta: GetConsultaProgramadaById,
    private val getTipoActividad: GetTipoActividadById,
    private val getEspecialidad: GetEspecialidadById,
    private val getTiposActividades: GetTiposActividades,
    private val getEspecialidades: GetEspecialidades,

    private val getDetalleVital: GetDetalleVitalByConsultaId,
    private val getDetalleAntropometrico: GetDetalleAntropometricoByConsultaId,
    private val getDetalleMetabolico: GetDetalleMetabolicoByConsultaId,
    private val getDetallePediatrico: GetDetallePediatricoByConsultaId,
    private val getDetalleObstetricia: GetDetalleObstetriciaByConsultaId,

    private val getRiesgosbiologicos: GetRiesgosBiologicos,
    private val getEnfermedades: GetEnfermedades,
    private val getDiagnosticosByConsultaId: GetDiagnosticosByConsultaId,
    private val saveConsulta: SaveConsulta,
    private val saveDetalleVital: SaveDetalleVital,
    private val saveDetalleAntropometrico: SaveDetalleAntropometrico,
    private val saveDetalleMetabolico: SaveDetalleMetabolico,
    private val saveDetalleObstetricia: SaveDetalleObstetricia,
    private val saveDetallePediatrico: SaveDetallePediatrico,
    private val saveDiagnosticos: SaveDiagnosticos,

    private val getParametroCrecimientoNinoEdad: GetParametroCrecimientoNinoEdad,
    private val getParametroCrecimientoPediatricoEdad: GetParametroCrecimientoPediatricoEdad,
    private val getParametroCrecimientoPediatricoLongitud: GetParametroCrecimientoPediatricoLongitud,

    private val sessionManager: SessionManager
) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private val _consulta = MutableLiveData<Consulta>()
    val consulta: LiveData<Consulta> = _consulta
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> = _idUsuarioInstitucion
    private val _modoConsulta = MutableLiveData<ModoConsulta>()
    val modoConsulta: LiveData<ModoConsulta> = _modoConsulta
    private val _consultaEditando = MutableLiveData<Consulta>()
    val consultaEditando: LiveData<Consulta> = _consultaEditando

    private val _tipoActividad = MutableLiveData<TipoActividad>()
    val tipoActividad: LiveData<TipoActividad> = _tipoActividad
    private val _especialidad = MutableLiveData<Especialidad>()
    val especialidad: LiveData<Especialidad> = _especialidad
    private val _tipoConsulta = MutableLiveData<TipoConsulta>()
    val tipoConsulta: LiveData<TipoConsulta> = _tipoConsulta
    private val _tiposActividades = MutableLiveData<List<TipoActividad>>()
    val tiposActividades: LiveData<List<TipoActividad>> = _tiposActividades
    private val _especialidades = MutableLiveData<List<Especialidad>>()
    val especialidades: LiveData<List<Especialidad>> = _especialidades
    private val _tiposConsultas = MutableLiveData<List<TipoConsulta>>()
    val tiposConsultas: LiveData<List<TipoConsulta>> = _tiposConsultas

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir
    
    /**
     * Determina si los campos de información general (tipo de actividad, especialidad, tipo de consulta)
     * son editables según el estado de la consulta.
     * 
     * Reglas:
     * - Sin consulta previa: Editables
     * - Cita PENDIENTE: No editables (información establecida en programación)
     * - Cita REPROGRAMADA: No editables (información establecida en programación)
     * - Consulta COMPLETADA: Editables (se puede modificar)
     * - Otros estados: Editables
     */
    fun sonCamposInformacionGeneralEditables(): Boolean {
        val consultaActual = consulta.value
        return when {
            consultaActual == null -> true // Sin consulta previa, se puede editar
            consultaActual.estado == Estado.PENDIENTE -> false // Cita programada, no editable
            consultaActual.estado == Estado.REPROGRAMADA -> false // Cita reprogramada, no editable
            consultaActual.estado == Estado.COMPLETADA -> true // Consulta completada, se puede editar
            else -> true // Otros estados, se puede editar
        }
    }

    fun onCreate(idPaciente: String, idConsulta: String?, isEditable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val institutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
                if (institutionId == null) {
                    _mensaje.value = "No se ha seleccionado una institución."
                    _salir.value = true
                    return@launch
                }
                _idUsuarioInstitucion.value = institutionId

                // Obtener paciente en paralelo
                val pacienteDeferred = async { getPaciente(institutionId, idPaciente) }

                if (idConsulta != null) {
                    // Obtener consulta
                    val consultaResult = withContext(Dispatchers.IO) { getConsulta(idConsulta) }
                    if (consultaResult == null) {
                        _mensaje.value = "No se encontró la consulta."
                        _salir.value = true
                        return@launch
                    }
                    _consulta.value = consultaResult

                    // Determinar el modo según estado y editable
                    _modoConsulta.value = when {
                        consultaResult.estado == Estado.COMPLETADA -> {
                            if (isEditable) ModoConsulta.EDITAR_CONSULTA else ModoConsulta.VER_CONSULTA
                        }
                        consultaResult.estado == Estado.PENDIENTE -> {
                            if (isEditable) ModoConsulta.CULMINAR_CITA else ModoConsulta.VER_CONSULTA
                        }
                        consultaResult.estado == Estado.REPROGRAMADA -> {
                            if (isEditable) ModoConsulta.CULMINAR_CITA else ModoConsulta.VER_CONSULTA
                        }
                        else -> {
                            if (isEditable) ModoConsulta.EDITAR_CONSULTA else ModoConsulta.VER_CONSULTA
                        }
                    }

                    // Obtener datos seleccionados
                    val tipoActividadDeferred =
                        async { getTipoActividad(consultaResult.tipoActividadId) }
                    val especialidadDeferred =
                        async { getEspecialidad(consultaResult.especialidadRemitenteId) }

                    _tipoActividad.value = tipoActividadDeferred.await()
                    _especialidad.value = especialidadDeferred.await()
                    _tipoConsulta.value = TipoConsulta.valueOf(
                        consultaResult.tipoConsulta?.name ?: TipoConsulta.CONSULTA_SUCESIVA.name
                    )

                    // Cargar catálogos si es editable o si no hay datos seleccionados
                    if (isEditable || consultaResult.tipoActividadId == null) {
                        val actividadesDeferred = async { getTiposActividades() }
                        val especialidadesDeferred = async { getEspecialidades() }

                        _tiposActividades.value = actividadesDeferred.await()
                        _especialidades.value = especialidadesDeferred.await()
                        _tiposConsultas.value = TipoConsulta.entries
                    }

                } else {
                    // Sin cita previa ⇒ creación desde cero
                    _modoConsulta.value = ModoConsulta.CREAR_SIN_CITA

                    val actividadesDeferred = async { getTiposActividades() }
                    val especialidadesDeferred = async { getEspecialidades() }

                    _tiposActividades.value = actividadesDeferred.await()
                    _especialidades.value = especialidadesDeferred.await()
                    _tiposConsultas.value = TipoConsulta.entries
                }

                // Esperar el paciente
                _paciente.value = pacienteDeferred.await() ?: run {
                    _mensaje.value = "No se encontró el paciente."
                    _salir.value = true
                    return@launch
                }

            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos: ${e.localizedMessage ?: "desconocido"}"
                _salir.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validarConsulta(
        tipoActividad: TipoActividad?,
        especialidad: Especialidad?,
        tipoConsulta: TipoConsulta?
    ): Boolean {
        val errores = mutableMapOf<String, String>()

        // Solo validar si los campos son editables
        if (sonCamposInformacionGeneralEditables()) {
            if (tipoActividad == null) errores["tipoActividad"] = "Selecciona un tipo de actividad"
            if (especialidad == null) errores["especialidad"] = "Selecciona una especialidad"
            if (tipoConsulta == null) errores["tipoConsulta"] = "Selecciona un tipo de consulta"
        } else {
            // Si no son editables, verificar que los valores existentes estén disponibles
            if (tipoActividad == null) errores["tipoActividad"] = "Falta información del tipo de actividad"
            if (especialidad == null) errores["especialidad"] = "Falta información de la especialidad"
            if (tipoConsulta == null) errores["tipoConsulta"] = "Falta información del tipo de consulta"
        }

        _errores.value = errores
        return errores.isEmpty()
    }

    fun guardarConsultaParcial(
        tipoActividad: TipoActividad,
        especialidad: Especialidad,
        tipoConsulta: TipoConsulta,
        motivo: String?
    ) {
        val consultaExistente = consulta.value
        val idConsulta = consultaExistente?.id ?: Utils.generarUUID()
        val idUsuarioInst = idUsuarioInstitucion.value ?: 0

        val nuevaConsulta = Consulta(
            id = idConsulta,
            usuarioInstitucionId = idUsuarioInst,
            pacienteId = paciente.value?.id ?: "",
            tipoActividadId = tipoActividad.id,
            especialidadRemitenteId = especialidad.id,
            tipoConsulta = tipoConsulta,
            motivoConsulta = motivo?.takeIf { it.isNotBlank() },
            fechaHoraProgramada = consultaExistente?.fechaHoraProgramada,
            observaciones = consultaExistente?.observaciones,
            planes = consultaExistente?.planes,
            fechaHoraReal = consultaExistente?.fechaHoraReal,
            estado = consultaExistente?.estado ?: Estado.SIN_PREVIA_CITA,
            updatedAt = LocalDateTime.now()
        )
        _consultaEditando.value = nuevaConsulta
        Log.d("RegistrarConsultaViewModel", "Nueva consulta guardada: $nuevaConsulta")
    }

    // Fragment 2

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

    fun cargarDatosClinicos() {
        val idConsulta = consulta.value?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val vitalDeferred = async { getDetalleVital(idConsulta) }
                val antropDeferred = async { getDetalleAntropometrico(idConsulta) }
                val metabDeferred = async { getDetalleMetabolico(idConsulta) }
                val pedDeferred = async { getDetallePediatrico(idConsulta) }
                val obstDeferred = async { getDetalleObstetricia(idConsulta) }

                _detalleVital.value = vitalDeferred.await()
                _detalleAntropometrico.value = antropDeferred.await()
                _detalleMetabolico.value = metabDeferred.await()
                _detallePediatrico.value = pedDeferred.await()
                _detalleObstetricia.value = obstDeferred.await()

            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos clínicos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun guardarSignosVitales(
        frecuenciaCardiaca: Int?,
        presionSistolica: Int?,
        presionDiastolica: Int?,
        frecuenciaRespiratoria: Int?,
        temperatura: Double?,
        saturacionOxigeno: Int?,
        pulso: Int?
    ) {
        val idConsulta = consulta.value?.id ?: return
        val idExistente = detalleVital.value?.id
        val detalle = DetalleVital(
            id = idExistente ?: Utils.generarUUID(),
            consultaId = idConsulta,
            tensionArterialSistolica = presionSistolica,
            tensionArterialDiastolica = presionDiastolica,
            frecuenciaCardiaca = frecuenciaCardiaca,
            frecuenciaRespiratoria = frecuenciaRespiratoria,
            temperatura = temperatura,
            saturacionOxigeno = saturacionOxigeno,
            pulso = pulso,
            updatedAt = LocalDateTime.now()
        )

        _detalleVital.value = detalle
    }

    fun guardarDatosAntropometricos(
        peso: Double?,
        altura: Double?,
        talla: Double?,
        circunferenciaBraquial: Double?,
        circunferenciaCadera: Double?,
        circunferenciaCintura: Double?,
        perimetroCefalico: Double?,
        pliegueTricipital: Double?,
        pliegueSubescapular: Double?
    ) {
        val idConsulta = consulta.value?.id ?: return
        val idExistente = detalleAntropometrico.value?.id
        val detalle = DetalleAntropometrico(
            id = idExistente ?: Utils.generarUUID(),
            consultaId = idConsulta,
            peso = peso,
            altura = altura,
            talla = talla,
            circunferenciaBraquial = circunferenciaBraquial,
            circunferenciaCadera = circunferenciaCadera,
            circunferenciaCintura = circunferenciaCintura,
            perimetroCefalico = perimetroCefalico,
            pliegueTricipital = pliegueTricipital,
            pliegueSubescapular = pliegueSubescapular,
            updatedAt = LocalDateTime.now()
        )

        _detalleAntropometrico.value = detalle
    }

    fun guardarDatosMetabolicos(
        glicemiaBasal: Int?,
        glicemiaPostprandial: Int?,
        glicemiaAleatoria: Int?,
        hemoglobinaGlicosilada: Double?,
        trigliceridos: Int?,
        colesterolTotal: Int?,
        colesterolHdl: Int?,
        colesterolLdl: Int?
    ) {
        val idConsulta = consulta.value?.id ?: return
        val idExistente = detalleMetabolico.value?.id
        val detalle = DetalleMetabolico(
            id = idExistente ?: Utils.generarUUID(),
            consultaId = idConsulta,
            glicemiaBasal = glicemiaBasal,
            glicemiaPostprandial = glicemiaPostprandial,
            glicemiaAleatoria = glicemiaAleatoria,
            hemoglobinaGlicosilada = hemoglobinaGlicosilada,
            trigliceridos = trigliceridos,
            colesterolTotal = colesterolTotal,
            colesterolHdl = colesterolHdl,
            colesterolLdl = colesterolLdl,
            updatedAt = LocalDateTime.now()
        )

        _detalleMetabolico.value = detalle
    }

    fun guardarDatosPediatricos(usaBiberon: Boolean?, tipoLactancia: TipoLactancia?) {
        val idConsulta = consulta.value?.id ?: return
        val idExistente = detallePediatrico.value?.id
        val detalle = DetallePediatrico(
            id = idExistente ?: Utils.generarUUID(),
            consultaId = idConsulta,
            usaBiberon = usaBiberon,
            tipoLactancia = tipoLactancia,
            updatedAt = LocalDateTime.now()
        )

        _detallePediatrico.value = detalle
    }

    fun guardarDatosObstetricos(
        estaEmbarazada: Boolean?,
        fechaUltimaMenstruacion: LocalDate?,
        semanasGestacion: Int?,
        pesoPreEmbarazo: Double?
    ) {
        val idConsulta = consulta.value?.id ?: return
        val idExistente = detalleObstetricia.value?.id
        val detalle = DetalleObstetricia(
            id = idExistente ?: Utils.generarUUID(),
            consultaId = idConsulta,
            estaEmbarazada = estaEmbarazada,
            fechaUltimaMenstruacion = fechaUltimaMenstruacion,
            semanasGestacion = semanasGestacion,
            pesoPreEmbarazo = pesoPreEmbarazo,
            updatedAt = LocalDateTime.now()
        )

        _detalleObstetricia.value = detalle
    }

    // Fragment 3

    private val _riesgosBiologicosDisponibles = MutableLiveData<List<RiesgoBiologico>>()
    val riesgosBiologicosDisponibles: LiveData<List<RiesgoBiologico>> = _riesgosBiologicosDisponibles
    private val _riesgosBiologicosSeleccionados = MediatorLiveData<List<RiesgoBiologico>>()
    val riesgosBiologicosSeleccionados: LiveData<List<RiesgoBiologico>> = _riesgosBiologicosSeleccionados
    private val _enfermedades = MutableLiveData<List<Enfermedad>>()
    val enfermedades: LiveData<List<Enfermedad>> = _enfermedades

    private val _riesgoBiologico = MutableLiveData<RiesgoBiologico>()
    val riesgoBiologico: LiveData<RiesgoBiologico> = _riesgoBiologico
    private val _enfermedad = MutableLiveData<Enfermedad>()
    val enfermedad: LiveData<Enfermedad> = _enfermedad

    private val _diagnosticosConsulta = MutableLiveData<List<DiagnosticoEntity>>()

    init {
        _riesgosBiologicosSeleccionados.addSource(_diagnosticosConsulta) { mapearDiagnosticosYRiesgos() }
        _riesgosBiologicosSeleccionados.addSource(_riesgosBiologicosDisponibles) { mapearDiagnosticosYRiesgos() }
    }

    private fun mapearDiagnosticosYRiesgos() {
        val diagnosticos = _diagnosticosConsulta.value.orEmpty()
        val catalogo = _riesgosBiologicosDisponibles.value.orEmpty()
        val riesgosSeleccionados = diagnosticos
            .filter { it.riesgoBiologicoId != null }
            .mapNotNull { diag -> catalogo.find { it.id == diag.riesgoBiologicoId } }
        _riesgosBiologicosSeleccionados.value = riesgosSeleccionados
    }

    fun guardarConsultaCompleta(observaciones: String?, planes: String?) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val consultaActual = consultaEditando.value
                    ?: run {
                        _mensaje.value = "Consulta no válida para guardar"
                        return@launch
                    }

                // Crear una copia actualizada con observaciones, planes y fechaHoraReal
                val consultaActualizada = consultaActual.copy(
                    estado = if (consultaActual.estado != Estado.SIN_PREVIA_CITA) Estado.COMPLETADA else consultaActual.estado,
                    observaciones = observaciones?.takeIf { it.isNotBlank() },
                    planes = planes?.takeIf { it.isNotBlank() },
                    fechaHoraReal = consultaActual.fechaHoraReal ?: LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                // Actualizar el LiveData con la consulta actualizada
                _consultaEditando.value = consultaActualizada
                Log.d("RegistrarConsultaViewModel", "Consulta actualizada: $consultaActualizada")
                // Guardar consulta principal
                saveConsulta(consultaActualizada)

                // Guardar detalles si están disponibles
                detalleVital.value?.let { saveDetalleVital(it) }
                detalleAntropometrico.value?.let { saveDetalleAntropometrico(it) }
                detalleMetabolico.value?.let { saveDetalleMetabolico(it) }
                detalleObstetricia.value?.let { saveDetalleObstetricia(it) }
                detallePediatrico.value?.let { saveDetallePediatrico(it) }

                // Guardar diagnósticos (riesgos biológicos)
                val diagnosticos = riesgosBiologicosSeleccionados.value.orEmpty().map { riesgo ->
                    DiagnosticoEntity(
                        id = Utils.generarUUID(),
                        consultaId = consultaActualizada.id,
                            riesgoBiologicoId = riesgo.id,
                        enfermedadId = null,
                        isPrincipal = false,
                        updatedAt = LocalDateTime.now()
                    )
                }
                saveDiagnosticos(consultaActualizada.id, diagnosticos)

                _mensaje.value = "Consulta guardada correctamente"
                _salir.value = true

            } catch (e: Exception) {
                _mensaje.value = "Error al guardar consulta: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarRiesgosBiologicosDisponibles() {
        // Evitar cargar si ya están cargados, excepto en modo editar
        if (_riesgosBiologicosDisponibles.value != null && modoConsulta.value != ModoConsulta.EDITAR_CONSULTA) {
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val pacienteActual = paciente.value
                if (pacienteActual != null) {
                    val edadMeses = Utils.calcularEdadEnMeses(pacienteActual.fechaNacimiento)
                    val riesgosDisponibles =
                        getRiesgosbiologicos(pacienteActual.genero.first().uppercaseChar().toString(), edadMeses)
                    _riesgosBiologicosDisponibles.value = riesgosDisponibles
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar riesgos biológicos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarRiesgosBiologicosExistentes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val idConsulta = consulta.value?.id
                if (idConsulta != null) {
                    val diagnosticos = getDiagnosticosByConsultaId(idConsulta)
                    _diagnosticosConsulta.value = diagnosticos
                    // Siempre cargar los riesgos disponibles para poder mapear correctamente
                    cargarRiesgosBiologicosDisponibles()
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar riesgos biológicos existentes: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarRiesgoBiologico(riesgoBiologico: RiesgoBiologico) {
        val riesgosActuales = _riesgosBiologicosSeleccionados.value.orEmpty().toMutableList()
        if (!riesgosActuales.any { it.id == riesgoBiologico.id }) {
            riesgosActuales.add(riesgoBiologico)
            _riesgosBiologicosSeleccionados.value = riesgosActuales
        }
    }

    fun eliminarRiesgoBiologico(riesgoBiologico: RiesgoBiologico) {
        val riesgosActuales = _riesgosBiologicosSeleccionados.value.orEmpty().toMutableList()
        riesgosActuales.removeAll { it.id == riesgoBiologico.id }
        _riesgosBiologicosSeleccionados.value = riesgosActuales
    }

}