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
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class SignosVitalesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "consulta_id") val consultaId: Int,
    @ColumnInfo(name = "peso") val peso: Double,
    @ColumnInfo(name = "altura") val altura: Double,
    @ColumnInfo(name = "temperatura") val temperatura: Double,
    @ColumnInfo(name = "glicemia") val glicemia: Int,
    @ColumnInfo(name = "pulso") val pulso: Int,
    @ColumnInfo(name = "tension_arterial") val tensionArterial: String,
    @ColumnInfo(name = "frecuencia_cardiaca") val frecuenciaCardiaca: Int,
    @ColumnInfo(name = "frecuencia_respiratoria") val frecuenciaRespiratoria: Int,
    @ColumnInfo(name = "saturacion_oxigeno") val saturacionOxigeno: Int,
    @ColumnInfo(name = "perimetro_cefalico") val perimetroCefalico: Double,
    @ColumnInfo(name = "circunferencia_braquial") val circunferenciaBraquial: Double,
    @ColumnInfo(name = "circunferencia_cintura") val circunferenciaCintura: Double,
    @ColumnInfo(name = "isEmbarazo") val isEmbarazo: Boolean,
    @ColumnInfo(name = "fecha_ultima_menstruacion") val fechaUltimaMenstruacion: String,
    @ColumnInfo(name = "semanas_gestacion") val semanasGestacion: Int,
    @ColumnInfo(name = "tipo_lactancia") val tipoLactancia: String,
    @ColumnInfo(name = "isTetero") val isTetero: Boolean,
    @ColumnInfo(name = "relacion_peso_altura") val relacionPesoAltura: Double,
    @ColumnInfo(name = "relacion_altura_edad") val relacionAlturaEdad: Double,
    @ColumnInfo(name = "relacion_peso_edad") val relacionPesoEdad: Double
)

fun SignosVitales.toEntity() = SignosVitalesEntity(
    consultaId = consultaId,
    peso = peso,
    altura = altura,
    temperatura = temperatura,
    glicemia = glicemia,
    pulso = pulso,
    tensionArterial = tensionArterial,
    frecuenciaCardiaca = frecuenciaCardiaca,
    frecuenciaRespiratoria = frecuenciaRespiratoria,
    saturacionOxigeno = saturacionOxigeno,
    perimetroCefalico = perimetroCefalico,
    circunferenciaBraquial = circunferenciaBraquial,
    circunferenciaCintura = circunferenciaCintura,
    isEmbarazo = isEmbarazo,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    tipoLactancia = tipoLactancia,
    isTetero = isTetero,
    relacionPesoAltura = relacionPesoAltura,
    relacionAlturaEdad = relacionAlturaEdad,
    relacionPesoEdad = relacionPesoEdad
)