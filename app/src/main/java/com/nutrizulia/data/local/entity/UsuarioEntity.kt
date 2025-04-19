package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Usuario

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["cedula"], unique = true),
        Index(value = ["telefono"], unique = true),
        Index(value = ["correo"], unique = true)
              ],
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "cedula") val cedula: String,
    @ColumnInfo(name = "primer_nombre") val primerNombre: String,
    @ColumnInfo(name = "segundo_nombre") val segundoNombre: String,
    @ColumnInfo(name = "primer_apellido") val primerApellido: String,
    @ColumnInfo(name = "segundo_apellido") val segundoApellido: String,
    @ColumnInfo(name = "profesion") val profesion: String,
    @ColumnInfo(name = "telefono") val telefono: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "clave") val clave: String,
    @ColumnInfo(name = "isActivo") val isActivo: Boolean
)

fun Usuario.toEntity() = UsuarioEntity(
    cedula = cedula,
    primerNombre = primerNombre,
    segundoNombre = segundoNombre,
    primerApellido = primerApellido,
    segundoApellido = segundoApellido,
    profesion = profesion,
    telefono = telefono,
    correo = correo,
    clave = clave,
    isActivo = isActivo
)
