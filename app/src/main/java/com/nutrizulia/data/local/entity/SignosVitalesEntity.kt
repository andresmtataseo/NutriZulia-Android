package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.SignosVitales

@Entity(
    tableName = "signos_vitales",
    foreignKeys = [ForeignKey(
        entity = ConsultaEntity::class,
        parentColumns = ["id"],
        childColumns = ["consulta_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SignosVitalesEntity(
    @PrimaryKey
    @ColumnInfo(name = "consulta_id") val consultaId: Int,
    @ColumnInfo(name = "peso") val peso: Double,
    @ColumnInfo(name = "altura") val altura: Double,
    @ColumnInfo(name = "glicemia_basal") val glicemiaBasal: Int?,
    @ColumnInfo(name = "glicemia_postprandial") val glicemiaPostprandial: Int?,
    @ColumnInfo(name = "glicemia_aleatoria") val glicemiaAleatoria: Int?,
    @ColumnInfo(name = "hemoglobina_glicosilada") val hemoglobinaGlicosilada: Double?,
    @ColumnInfo(name = "trigliceridos") val trigliceridos: Int?,
    @ColumnInfo(name = "colesterol_total") val colesterolTotal: Int?,
    @ColumnInfo(name = "colesterol_hdl") val colesterolHdl: Int?,
    @ColumnInfo(name = "colesterol_ldl") val colesterolLdl: Int?,
    @ColumnInfo(name = "tension_arterial") val tensionArterial: String?,
    @ColumnInfo(name = "frecuencia_cardiaca") val frecuenciaCardiaca: Int?,
    @ColumnInfo(name = "pulso") val pulso: Int?,
    @ColumnInfo(name = "saturacion_oxigeno") val saturacionOxigeno: Int?,
    @ColumnInfo(name = "frecuencia_respiratoria") val frecuenciaRespiratoria: Int?,
    @ColumnInfo(name = "temperatura") val temperatura: Double?,
    @ColumnInfo(name = "circunferencia_braquial") val circunferenciaBraquial: Double?,
    @ColumnInfo(name = "circunferencia_cadera") val circunferenciaCadera: Double?,
    @ColumnInfo(name = "circunferencia_cintura") val circunferenciaCintura: Double?,
    @ColumnInfo(name = "perimetro_cefalico") val perimetroCefalico: Double?,
    @ColumnInfo(name = "isEmbarazo") val isEmbarazo: Boolean?,
    @ColumnInfo(name = "fecha_ultima_menstruacion") val fechaUltimaMenstruacion: String?,
    @ColumnInfo(name = "semanas_gestacion") val semanasGestacion: Int?,
    @ColumnInfo(name = "peso_pre_embarazo") val pesoPreEmbarazo: Double?,
    @ColumnInfo(name = "isTetero") val isTetero: Boolean?,
    @ColumnInfo(name = "tipo_lactancia") val tipoLactancia: String?
)

fun SignosVitales.toEntity() = SignosVitalesEntity(
    consultaId = consultaId,
    peso = peso,
    altura = altura,
    glicemiaBasal = glicemiaBasal,
    glicemiaPostprandial = glicemiaPostprandial,
    glicemiaAleatoria = glicemiaAleatoria,
    hemoglobinaGlicosilada = hemoglobinaGlicosilada,
    trigliceridos = trigliceridos,
    colesterolTotal = colesterolTotal,
    colesterolHdl = colesterolHdl,
    colesterolLdl = colesterolLdl,
    tensionArterial = tensionArterial,
    frecuenciaCardiaca = frecuenciaCardiaca,
    pulso = pulso,
    saturacionOxigeno = saturacionOxigeno,
    frecuenciaRespiratoria = frecuenciaRespiratoria,
    temperatura = temperatura,
    circunferenciaBraquial = circunferenciaBraquial,
    circunferenciaCadera = circunferenciaCadera,
    circunferenciaCintura = circunferenciaCintura,
    perimetroCefalico = perimetroCefalico,
    isEmbarazo = isEmbarazo,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    pesoPreEmbarazo = pesoPreEmbarazo,
    isTetero = isTetero,
    tipoLactancia = tipoLactancia
)