package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.ParametroCrecimientoPediatricoEdad

@Entity(
    tableName = "parametros_crecimientos_pediatricos_edad",
    indices = [
        Index(value = ["tipo_indicador_id"]),
        Index(value = ["grupo_etario_id"]),
        Index(value = ["tipo_indicador_id", "grupo_etario_id", "genero", "edad_dia"])
    ],

    foreignKeys = [
        ForeignKey(
            entity = TipoIndicadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipo_indicador_id"],
            onDelete = ForeignKey.NO_ACTION),
        ForeignKey(
            entity = GrupoEtarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["grupo_etario_id"],
            onDelete = ForeignKey.NO_ACTION)
    ]
)
data class ParametroCrecimientoPediatricoEdadEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "tipo_indicador_id") val tipoIndicadorId: Int,
    @ColumnInfo(name = "grupo_etario_id") val grupoEtarioId: Int,
    @ColumnInfo(name = "genero") val genero: String,
    @ColumnInfo(name = "edad_dia") val edadDia: Int,
    @ColumnInfo(name = "lambda") val lambda: Double,
    @ColumnInfo(name = "mu") val mu: Double,
    @ColumnInfo(name = "sigma") val sigma: Double
)

fun ParametroCrecimientoPediatricoEdad.toEntity() = ParametroCrecimientoPediatricoEdadEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    grupoEtarioId = grupoEtarioId,
    genero = genero,
    edadDia = edadDia,
    lambda = lambda,
    mu = mu,
    sigma = sigma
)