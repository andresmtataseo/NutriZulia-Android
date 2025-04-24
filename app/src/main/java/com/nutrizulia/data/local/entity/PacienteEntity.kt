package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.util.Utils.obtenerFechaActual
import javax.annotation.Nonnull

@Entity(
    tableName = "pacientes",
    indices = [
        Index(value = ["cedula"], unique = true),
        Index(value = ["cod_entidad_ine", "cod_municipio_ine", "cod_parroquia_ine", "id_comunidad_ine"]),
        Index(value = ["telefono"], unique = true),
        Index(value = ["correo"], unique = true)
    ],
    foreignKeys = [ForeignKey(
        entity = ComunidadEntity::class,
        parentColumns = ["cod_entidad_ine", "cod_municipio_ine", "cod_parroquia_ine", "id_comunidad_ine"],
        childColumns = ["cod_entidad_ine", "cod_municipio_ine", "cod_parroquia_ine", "id_comunidad_ine"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PacienteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "cedula") val cedula: String,
    @ColumnInfo(name = "primer_nombre") val primerNombre: String,
    @ColumnInfo(name = "segundo_nombre") val segundoNombre: String?,
    @ColumnInfo(name = "primer_apellido") val primerApellido: String,
    @ColumnInfo(name = "segundo_apellido") val segundoApellido: String,
    @ColumnInfo(name = "fecha_nacimiento") val fechaNacimiento: String,
    @ColumnInfo(name = "genero") val genero: String,
    @ColumnInfo(name = "etnia") val etnia: String,
    @ColumnInfo(name = "nacionalidad") val nacionalidad: String,
    @ColumnInfo(name = "cod_entidad_ine") val codEntidad: String,
    @ColumnInfo(name = "cod_municipio_ine") val codMunicipio: String,
    @ColumnInfo(name = "cod_parroquia_ine") val codParroquia: String,
    @ColumnInfo(name = "id_comunidad_ine") val idComunidad: String,
    @ColumnInfo(name = "telefono") val telefono: String?,
    @ColumnInfo(name = "correo") val correo: String?,
    @ColumnInfo(name = "fecha_ingreso") val fechaIngreso: String = obtenerFechaActual()
)

fun Paciente.toEntity() = PacienteEntity(
    cedula = cedula,
    primerNombre = primerNombre,
    segundoNombre = segundoNombre,
    primerApellido =primerApellido,
    segundoApellido = segundoApellido,
    fechaNacimiento = fechaNacimiento,
    genero = genero,
    etnia = etnia,
    nacionalidad = nacionalidad,
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    codParroquia = codParroquia,
    idComunidad = idComunidad,
    telefono = telefono,
    correo = correo,
    fechaIngreso = fechaIngreso
)
