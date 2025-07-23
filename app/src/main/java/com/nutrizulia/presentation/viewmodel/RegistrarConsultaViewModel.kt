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
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.domain.usecase.collection.GetDiagnosticosByConsultaId
import com.nutrizulia.util.Utils.calcularIMC
import com.nutrizulia.util.Utils.calcularZScoreOMS
import com.nutrizulia.util.Utils.ZScoreResult
import com.nutrizulia.util.Utils.ImcResult
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
    private val saveEvaluacionesAntropometricas: SaveEvaluacionesAntropometricas,

    private val getEvaluacionesAntropometricasByConsultaId: GetEvaluacionesAntropometricasByConsultaId,
    private val getGrupoEtario: GetGrupoEtario,
    private val getParametroCrecimientoNinoEdad: GetParametroCrecimientoNinoEdad,
    private val getParametroCrecimientoPediatricoEdad: GetParametroCrecimientoPediatricoEdad,
    private val getParametroCrecimientoPediatricoLongitud: GetParametroCrecimientoPediatricoLongitud,
    private val getReglaInterpretacionImc: GetReglaInterpretacionImc,
    //private val getReglasInterpretacionPercentil: GetReglasInterpretacionPercentil,
    private val getReglaInterpretacionZScore: GetReglaInterpretacionZScore,

    private val sessionManager: SessionManager
) : ViewModel() {

    // ... (sin cambios en las propiedades y funciones iniciales hasta guardarConsultaCompleta)

    // Propiedades
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

    // Fragment 1
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

        if (sonCamposInformacionGeneralEditables()) {
            if (tipoActividad == null) errores["tipoActividad"] = "Selecciona un tipo de actividad"
            if (especialidad == null) errores["especialidad"] = "Selecciona una especialidad"
            if (tipoConsulta == null) errores["tipoConsulta"] = "Selecciona un tipo de consulta"
        } else {
            if (tipoActividad == null) errores["tipoActividad"] =
                "Falta información del tipo de actividad"
            if (especialidad == null) errores["especialidad"] =
                "Falta información de la especialidad"
            if (tipoConsulta == null) errores["tipoConsulta"] =
                "Falta información del tipo de consulta"
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
        val idConsulta = consultaEditando.value?.id ?: return
        val idExistente = detalleVital.value?.id ?: Utils.generarUUID()
        val detalle = DetalleVital(
            id = idExistente,
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
        val idConsulta = consultaEditando.value?.id ?: return
        val idExistente = detalleAntropometrico.value?.id ?: Utils.generarUUID()
        val detalle = DetalleAntropometrico(
            id = idExistente,
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
        val idConsulta = consultaEditando.value?.id ?: return
        val idExistente = detalleMetabolico.value?.id ?: Utils.generarUUID()
        val detalle = DetalleMetabolico(
            id = idExistente,
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
        val idConsulta = consultaEditando.value?.id ?: return
        val idExistente = detallePediatrico.value?.id ?: Utils.generarUUID()
        val detalle = DetallePediatrico(
            id = idExistente,
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
        val idConsulta = consultaEditando.value?.id ?: return
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
    val riesgosBiologicosDisponibles: LiveData<List<RiesgoBiologico>> =
        _riesgosBiologicosDisponibles
    private val _riesgosBiologicosSeleccionados = MediatorLiveData<List<RiesgoBiologico>>()
    val riesgosBiologicosSeleccionados: LiveData<List<RiesgoBiologico>> =
        _riesgosBiologicosSeleccionados
    private val _enfermedades = MutableLiveData<List<Enfermedad>>()
    val enfermedades: LiveData<List<Enfermedad>> = _enfermedades

    private val _riesgoBiologico = MutableLiveData<RiesgoBiologico>()
    val riesgoBiologico: LiveData<RiesgoBiologico> = _riesgoBiologico
    private val _enfermedad = MutableLiveData<Enfermedad>()
    val enfermedad: LiveData<Enfermedad> = _enfermedad

    private val _diagnosticosConsulta = MutableLiveData<List<DiagnosticoEntity>>()

    private val _evaluacionesAntropometricas = MutableLiveData<List<EvaluacionAntropometrica>>()
    val evaluacionesAntropometricas: LiveData<List<EvaluacionAntropometrica>> =
        _evaluacionesAntropometricas


    init {
        _riesgosBiologicosSeleccionados.addSource(_diagnosticosConsulta) { mapearDiagnosticosYRiesgos() }
        _riesgosBiologicosSeleccionados.addSource(_riesgosBiologicosDisponibles) { mapearDiagnosticosYRiesgos() }
    }

    fun cargarDatosDiagnostico() {
        val currentMode = modoConsulta.value ?: return
        val pacienteActual = paciente.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (currentMode == ModoConsulta.VER_CONSULTA) {
                    val idConsulta = consulta.value?.id
                    if (idConsulta != null) {
                        coroutineScope {
                            val evaluacionesDeferred =
                                async { getEvaluacionesAntropometricasByConsultaId(idConsulta) }
                            val diagnosticosDeferred =
                                async { getDiagnosticosByConsultaId(idConsulta) }

                            _evaluacionesAntropometricas.value = evaluacionesDeferred.await()
                            _diagnosticosConsulta.value = diagnosticosDeferred.await()
                        }
                    }
                } else if (currentMode == ModoConsulta.EDITAR_CONSULTA) {
                    coroutineScope {
                        val riesgosDisponiblesDeferred = async {
                            val edadMeses =
                                Utils.calcularEdadEnMeses(pacienteActual.fechaNacimiento)
                            getRiesgosbiologicos(
                                pacienteActual.genero.first().uppercaseChar().toString(), edadMeses
                            )
                        }

                        val idConsulta = consulta.value?.id
                        if (idConsulta != null) {
                            val diagnosticosDeferred =
                                async { getDiagnosticosByConsultaId(idConsulta) }
                            _diagnosticosConsulta.value = diagnosticosDeferred.await()
                        }

                        _riesgosBiologicosDisponibles.value = riesgosDisponiblesDeferred.await()
                    }

                    realizarEvaluacionAntropometrica()

                } else {
                    coroutineScope {
                        val riesgosDisponiblesDeferred = async {
                            val edadMeses =
                                Utils.calcularEdadEnMeses(pacienteActual.fechaNacimiento)
                            getRiesgosbiologicos(
                                pacienteActual.genero.first().uppercaseChar().toString(), edadMeses
                            )
                        }

                        val idConsulta = consulta.value?.id
                        if (idConsulta != null) {
                            val evaluacionesDeferred =
                                async { getEvaluacionesAntropometricasByConsultaId(idConsulta) }
                            val diagnosticosDeferred =
                                async { getDiagnosticosByConsultaId(idConsulta) }

                            _evaluacionesAntropometricas.value = evaluacionesDeferred.await()
                            _diagnosticosConsulta.value = diagnosticosDeferred.await()
                        }

                        _riesgosBiologicosDisponibles.value = riesgosDisponiblesDeferred.await()
                    }

                    realizarEvaluacionAntropometrica()
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar datos de diagnóstico: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
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
                val consultaActual = consultaEditando.value ?: run {
                    _mensaje.value = "Consulta no válida para guardar"
                    _isLoading.value = false
                    return@launch
                }

                val consultaActualizada = consultaActual.copy(
                    estado = if (consultaActual.estado != Estado.SIN_PREVIA_CITA) Estado.COMPLETADA else consultaActual.estado,
                    observaciones = observaciones?.takeIf { it.isNotBlank() },
                    planes = planes?.takeIf { it.isNotBlank() },
                    fechaHoraReal = consultaActual.fechaHoraReal ?: LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                _consultaEditando.value = consultaActualizada
                saveConsulta(consultaActualizada)

                // Guardar detalles
                detalleVital.value?.let { saveDetalleVital(it) }
                detalleAntropometrico.value?.let { saveDetalleAntropometrico(it) }
                detalleMetabolico.value?.let { saveDetalleMetabolico(it) }
                detalleObstetricia.value?.let { saveDetalleObstetricia(it) }
                detallePediatrico.value?.let { saveDetallePediatrico(it) }

                // Guardar evaluaciones antropométricas
                Log.w("GUARDAR EVALUACIONES", evaluacionesAntropometricas.value.toString())
                Log.w("GUARDAR EVALUACIONES", evaluacionesAntropometricas.value?.size.toString())
                evaluacionesAntropometricas.value?.let { saveEvaluacionesAntropometricas(it) }

                // Guardar diagnósticos de riesgos biológicos
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

//    fun cargarRiesgosBiologicosDisponibles() {
//        if (_riesgosBiologicosDisponibles.value != null && modoConsulta.value != ModoConsulta.EDITAR_CONSULTA) {
//            return
//        }
//
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val pacienteActual = paciente.value
//                if (pacienteActual != null) {
//                    val edadMeses = Utils.calcularEdadEnMeses(pacienteActual.fechaNacimiento)
//                    val riesgosDisponibles =
//                        getRiesgosbiologicos(
//                            pacienteActual.genero.first().uppercaseChar().toString(), edadMeses
//                        )
//                    _riesgosBiologicosDisponibles.value = riesgosDisponibles
//                }
//            } catch (e: Exception) {
//                _mensaje.value = "Error al cargar riesgos biológicos: ${e.localizedMessage}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun cargarRiesgosBiologicosExistentes() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val idConsulta = consulta.value?.id
//                if (idConsulta != null) {
//                    val diagnosticos = getDiagnosticosByConsultaId(idConsulta)
//                    _diagnosticosConsulta.value = diagnosticos
//                    // Siempre cargar los riesgos disponibles para poder mapear correctamente
//                    cargarRiesgosBiologicosDisponibles()
//                }
//            } catch (e: Exception) {
//                _mensaje.value =
//                    "Error al cargar riesgos biológicos existentes: ${e.localizedMessage}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

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

    fun realizarEvaluacionAntropometrica() {
        viewModelScope.launch {
            runCatching {
                val pacienteActual = paciente.value ?: throw IllegalStateException("Paciente no disponible")

                val detalleAntropometrico = _detalleAntropometrico.value ?: throw IllegalStateException("Datos antropométricos no disponibles")

                val consultaActual = consultaEditando.value ?: throw IllegalStateException("Consulta no disponible")

                val edadMeses = Utils.calcularEdadEnMeses(pacienteActual.fechaNacimiento)

                val grupoEtario = getGrupoEtario(edadMeses) ?: throw IllegalStateException("Grupo etario no encontrado para la edad de $edadMeses meses")

                val evaluacionesActuales = _evaluacionesAntropometricas.value.orEmpty().toMutableList()

                when (grupoEtario.id) {
                    1 -> evaluateInfant(
                        pacienteActual,
                        detalleAntropometrico,
                        consultaActual,
                        grupoEtario,
                        evaluacionesActuales
                    )

                    2, 3 -> evaluateChildAndAdolescent(
                        pacienteActual,
                        detalleAntropometrico,
                        consultaActual,
                        grupoEtario,
                        evaluacionesActuales
                    )

                    else -> evaluateAdult(
                        detalleAntropometrico,
                        consultaActual,
                        evaluacionesActuales
                    )
                }

                _evaluacionesAntropometricas.postValue(evaluacionesActuales)

            }.onFailure { e ->
                _mensaje.postValue("Error en evaluación antropométrica: ${e.localizedMessage}")
                Log.e("EvaluacionAntro", "Error en evaluación", e)
            }
        }
    }

    private suspend fun evaluateInfant(
        paciente: Paciente,
        detalle: DetalleAntropometrico,
        consulta: Consulta,
        grupoEtario: GrupoEtario,
        evaluaciones: MutableList<EvaluacionAntropometrica>
    ) {
        val genero = paciente.genero.first().uppercaseChar().toString()
        val edadDias = Utils.calcularEdadEnDias(paciente.fechaNacimiento)
        val longitud = detalle.talla ?: detalle.altura
        ?: throw IllegalStateException("Debe registrar talla o altura para la evaluación antropométrica")
        val tipoMedicion = if (detalle.altura != null) "A" else "T"

        getParametroCrecimientoPediatricoEdad(grupoEtario.id, genero, edadDias).forEach { param ->
            val valorParaEvaluar: Double? = when (param.tipoIndicadorId) {
                1 -> if (detalle.peso != null) calcularIMC(detalle.peso, longitud).imc else null
                2 -> detalle.perimetroCefalico
                4 -> detalle.peso
                6 -> detalle.talla
                7 -> detalle.altura
                else -> null
            }
            if (valorParaEvaluar != null) {
                processZScoreEvaluation(
                    value = valorParaEvaluar,
                    tipoIndicadorId = param.tipoIndicadorId,
                    lambda = param.lambda,
                    mu = param.mu,
                    sigma = param.sigma,
                    consultaId = consulta.id,
                    detalleId = detalle.id,
                    evaluations = evaluaciones
                )
            }
        }

        getParametroCrecimientoPediatricoLongitud(grupoEtario.id, genero, longitud, tipoMedicion)?.let { param ->
            if (detalle.peso != null) {
                processZScoreEvaluation(
                    value = detalle.peso,
                    tipoIndicadorId = param.tipoIndicadorId,
                    lambda = param.lambda,
                    mu = param.mu,
                    sigma = param.sigma,
                    consultaId = consulta.id,
                    detalleId = detalle.id,
                    evaluations = evaluaciones
                )
            }
        }
    }

    private suspend fun evaluateChildAndAdolescent(
        paciente: Paciente,
        detalle: DetalleAntropometrico,
        consulta: Consulta,
        grupoEtario: GrupoEtario,
        evaluaciones: MutableList<EvaluacionAntropometrica>
    ) {
        val genero = paciente.genero.first().uppercaseChar().toString()
        val edadMeses = Utils.calcularEdadEnMeses(paciente.fechaNacimiento)
        val altura = detalle.altura
            ?: throw IllegalStateException("Debe registrar la altura para la evaluación antropométrica")

        val todosLosParametros = getParametroCrecimientoNinoEdad(2, genero, edadMeses) +
                getParametroCrecimientoNinoEdad(3, genero, edadMeses)

        todosLosParametros.forEach { param ->
            val valorParaEvaluar: Double? = when (param.tipoIndicadorId) {
                1 -> if (detalle.peso != null) calcularIMC(detalle.peso, altura).imc else null
                4 -> detalle.peso
                7 -> altura
                else -> null
            }
            if (valorParaEvaluar != null) {
                processZScoreEvaluation(
                    value = valorParaEvaluar,
                    tipoIndicadorId = param.tipoIndicadorId,
                    lambda = param.lambda,
                    mu = param.mu,
                    sigma = param.sigma,
                    consultaId = consulta.id,
                    detalleId = detalle.id,
                    evaluations = evaluaciones
                )
            }
        }
    }

    private suspend fun evaluateAdult(
        detalle: DetalleAntropometrico,
        consulta: Consulta,
        evaluations: MutableList<EvaluacionAntropometrica>
    ) {
        if (detalle.peso != null && detalle.altura != null) {
            val imcResult = calcularIMC(detalle.peso, detalle.altura)
            val diagnostico = getReglaInterpretacionImc(8, imcResult.imc) ?: "Sin diagnóstico"
            upsertEvaluationInList(
                evaluations,
                8,
                TipoValorCalculado.IMC,
                imcResult.imc,
                diagnostico,
                consulta.id,
                detalle.id
            )
        }
    }

    private suspend fun processZScoreEvaluation(
        value: Double,
        tipoIndicadorId: Int,
        lambda: Double,
        mu: Double,
        sigma: Double,
        consultaId: String,
        detalleId: String,
        evaluations: MutableList<EvaluacionAntropometrica>
    ) {
        val zScoreResult = calcularZScoreOMS(value, lambda, mu, sigma)
        zScoreResult?.let { result ->
            val diagnostic = getReglaInterpretacionZScore(tipoIndicadorId, result.zScore)
                ?: "Sin diagnóstico"

            upsertEvaluationInList(
                evaluations = evaluations,
                indicatorId = tipoIndicadorId,
                valueType = TipoValorCalculado.Z_SCORE,
                calculatedValue = result.zScore,
                diagnostic = diagnostic,
                consultaId = consultaId,
                detalleId = detalleId
            )
            upsertEvaluationInList(
                evaluations = evaluations,
                indicatorId = tipoIndicadorId,
                valueType = TipoValorCalculado.PERCENTIL,
                calculatedValue = result.percentil,
                diagnostic = diagnostic,
                consultaId = consultaId,
                detalleId = detalleId
            )
        }
    }

    private fun upsertEvaluationInList(
        evaluations: MutableList<EvaluacionAntropometrica>,
        indicatorId: Int,
        valueType: TipoValorCalculado,
        calculatedValue: Double,
        diagnostic: String,
        consultaId: String,
        detalleId: String
    ) {
        val updatedAt = LocalDateTime.now()
        val existingEvaluation = evaluations.find {
            it.tipoIndicadorId == indicatorId && it.tipoValorCalculado == valueType
        }

        if (existingEvaluation != null) {
            val index = evaluations.indexOf(existingEvaluation)
            evaluations[index] = existingEvaluation.copy(
                valorCalculado = calculatedValue,
                diagnosticoAntropometrico = diagnostic,
                updatedAt = updatedAt
            )
        } else {
            evaluations.add(
                EvaluacionAntropometrica(
                    id = Utils.generarUUID(),
                    consultaId = consultaId,
                    detalleAntropometricoId = detalleId,
                    tipoIndicadorId = indicatorId,
                    valorCalculado = calculatedValue,
                    tipoValorCalculado = valueType,
                    diagnosticoAntropometrico = diagnostic,
                    fechaEvaluacion = LocalDate.now(),
                    updatedAt = updatedAt
                )
            )
        }
    }

}