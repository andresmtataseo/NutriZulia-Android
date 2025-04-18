package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Paciente

@Entity(
    tableName = "pacientes",
    indices = [
        Index(value = ["cedula"], unique = true),
        Index(value = ["parroquias_id"])
    ],
//    foreignKeys = [ForeignKey(
//        entity = ParroquiaEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["parroquias_id"],
//        onDelete = ForeignKey.NO_ACTION
//    )]
)
data class PacienteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "cedula") val cedula: String,
    @ColumnInfo(name = "primer_nombre") val primerNombre: String,
    @ColumnInfo(name = "segundo_nombre") val segundoNombre: String,
    @ColumnInfo(name = "primer_apellido") val primerApellido: String,
    @ColumnInfo(name = "segundo_apellido") val segundoApellido: String,
    @ColumnInfo(name = "fecha_nacimiento") val fechaNacimiento: String,
    @ColumnInfo(name = "genero") val genero: String,
    @ColumnInfo(name = "etnia") val etnia: String,
    @ColumnInfo(name = "nacionalidad") val nacionalidad: String,
    @ColumnInfo(name = "grupo_sanguineo") val grupoSanguineo: String,
    @ColumnInfo(name = "parroquias_id") val parroquiasId: Int, // Clave foránea a la tabla Parroquia
    @ColumnInfo(name = "telefono") val telefono: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "fecha_ingreso") val fechaIngreso: String
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
    grupoSanguineo = grupoSanguineo,
    parroquiasId = parroquia,
    telefono = telefono,
    correo = correo,
    fechaIngreso = fechaIngreso)
