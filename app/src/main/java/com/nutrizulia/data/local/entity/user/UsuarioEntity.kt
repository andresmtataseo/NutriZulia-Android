package com.nutrizulia.data.local.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.user.Usuario
import java.time.LocalDate

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "cedula") val cedula: String,
    @ColumnInfo(name = "nombres") val nombres: String,
    @ColumnInfo(name = "apellidos") val apellidos: String,
    @ColumnInfo(name = "fecha_nacimiento") val fechaNacimiento: LocalDate,
    @ColumnInfo(name = "genero") val genero: String,
    @ColumnInfo(name = "telefono") val telefono: String?,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "clave") val clave: String,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean
)

fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    cedula = cedula,
    nombres = nombres,
    apellidos = apellidos,
    fechaNacimiento = fechaNacimiento,
    genero = genero,
    telefono = telefono,
    correo = correo,
    clave = clave,
    isEnabled = isEnabled
)