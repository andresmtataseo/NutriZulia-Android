package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.*
import com.nutrizulia.data.local.enum.TipoLactancia
import com.nutrizulia.domain.model.collection.*
import com.nutrizulia.domain.usecase.collection.*
import com.nutrizulia.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DatosClinicosViewModel @Inject constructor(
    private val getDetalleVital: GetDetalleVitalByConsultaId,
    private val getDetalleAntropometrico: GetDetalleAntropometricoByConsultaId,
    private val getDetalleMetabolico: GetDetalleMetabolicoByConsultaId,
    private val getDetallePediatrico: GetDetallePediatricoByConsultaId,
    private val getDetalleObstetricia: GetDetalleObstetriciaByConsultaId
) : ViewModel() {

    private val _initialDetalleVital = MutableLiveData<DetalleVital?>()
    val initialDetalleVital: LiveData<DetalleVital?> = _initialDetalleVital

    private val _initialDetalleAntropometrico = MutableLiveData<DetalleAntropometrico?>()
    val initialDetalleAntropometrico: LiveData<DetalleAntropometrico?> = _initialDetalleAntropometrico

    private val _initialDetalleMetabolico = MutableLiveData<DetalleMetabolico?>()
    val initialDetalleMetabolico: LiveData<DetalleMetabolico?> = _initialDetalleMetabolico

    private val _initialDetallePediatrico = MutableLiveData<DetallePediatrico?>()
    val initialDetallePediatrico: LiveData<DetallePediatrico?> = _initialDetallePediatrico

    private val _initialDetalleObstetricia = MutableLiveData<DetalleObstetricia?>()
    val initialDetalleObstetricia: LiveData<DetalleObstetricia?> = _initialDetalleObstetricia

    fun loadClinicalData(consultaId: String) {
        viewModelScope.launch {
            coroutineScope {
                val vitalDeferred = async { getDetalleVital(consultaId) }
                val antropDeferred = async { getDetalleAntropometrico(consultaId) }
                val metabolicoDeferred = async { getDetalleMetabolico(consultaId) }
                val pediatricoDeferred = async { getDetallePediatrico(consultaId) }
                val obstriciaDeferred = async { getDetalleObstetricia(consultaId) }

                _initialDetalleVital.value = vitalDeferred.await()
                _initialDetalleAntropometrico.value = antropDeferred.await()
                _initialDetalleMetabolico.value = metabolicoDeferred.await()
                _initialDetallePediatrico.value = pediatricoDeferred.await()
                _initialDetalleObstetricia.value = obstriciaDeferred.await()
            }
        }
    }

    fun createDetalleVital(
        idConsulta: String,
        existingId: String?,
        frecuenciaCardiaca: Int?,
        presionSistolica: Int?,
        presionDiastolica: Int?,
        frecuenciaRespiratoria: Int?,
        temperatura: Double?,
        saturacionOxigeno: Int?,
        pulso: Int?
    ): DetalleVital {
        return DetalleVital(
            id = existingId ?: Utils.generarUUID(),
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
    }

    fun createDetalleAntropometrico(
        idConsulta: String,
        existingId: String?,
        peso: Double?,
        altura: Double?,
        talla: Double?,
        circunferenciaBraquial: Double?,
        circunferenciaCadera: Double?,
        circunferenciaCintura: Double?,
        perimetroCefalico: Double?,
        pliegueTricipital: Double?,
        pliegueSubescapular: Double?
    ): DetalleAntropometrico {
        return DetalleAntropometrico(
            id = existingId ?: Utils.generarUUID(),
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
    }

    fun createDetalleMetabolico(
        idConsulta: String,
        existingId: String?,
        glicemiaBasal: Int?,
        glicemiaPostprandial: Int?,
        glicemiaAleatoria: Int?,
        hemoglobinaGlicosilada: Double?,
        trigliceridos: Int?,
        colesterolTotal: Int?,
        colesterolHdl: Int?,
        colesterolLdl: Int?
    ): DetalleMetabolico {
        return DetalleMetabolico(
            id = existingId ?: Utils.generarUUID(),
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
    }

    fun createDetallePediatrico(
        idConsulta: String,
        existingId: String?,
        usaBiberon: Boolean?,
        tipoLactancia: TipoLactancia?
    ): DetallePediatrico {
        return DetallePediatrico(
            id = existingId ?: Utils.generarUUID(),
            consultaId = idConsulta,
            usaBiberon = usaBiberon,
            tipoLactancia = tipoLactancia,
            updatedAt = LocalDateTime.now()
        )
    }

    fun createDetalleObstetricia(
        idConsulta: String,
        existingId: String?,
        estaEmbarazada: Boolean?,
        fechaUltimaMenstruacion: LocalDate?,
        semanasGestacion: Int?,
        pesoPreEmbarazo: Double?
    ): DetalleObstetricia {
        return DetalleObstetricia(
            id = existingId ?: Utils.generarUUID(),
            consultaId = idConsulta,
            estaEmbarazada = estaEmbarazada,
            fechaUltimaMenstruacion = fechaUltimaMenstruacion,
            semanasGestacion = semanasGestacion,
            pesoPreEmbarazo = pesoPreEmbarazo,
            updatedAt = LocalDateTime.now()
        )
    }

}