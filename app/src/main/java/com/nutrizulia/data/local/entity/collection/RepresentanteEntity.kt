package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.EtniaEntity
import com.nutrizulia.data.local.entity.catalog.NacionalidadEntity
import com.nutrizulia.data.local.entity.catalog.ParroquiaEntity
import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity
import com.nutrizulia.domain.model.collection.Representante
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "representantes",
    indices = [
        Index(value = ["cedula"], unique = true),
        Index(value = ["usuario_institucion_id"]),
        Index(value = ["etnia_id"]),
        Index(value = ["nacionalidad_id"]),
        Index(value = ["parroquia_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioInstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_institucion_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = EtniaEntity::class,
            parentColumns = ["id"],
            childColumns = ["etnia_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = NacionalidadEntity::class,
            parentColumns = ["id"],
            childColumns = ["nacionalidad_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ParroquiaEntity::class,
            parentColumns = ["id"],
            childColumns = ["parroquia_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
    ]
)
data class RepresentanteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_institucion_id") val usuarioInstitucionId: Int,
    @ColumnInfo(name = "cedula") val cedula: String,
    @ColumnInfo(name = "nombres") val nombres: String,
    @ColumnInfo(name = "apellidos") val apellidos: String,
    @ColumnInfo(name = "fecha_nacimiento") val fechaNacimiento: LocalDate,
    @ColumnInfo(name = "genero") val genero: String,
    @ColumnInfo(name = "etnia_id") val etniaId: Int,
    @ColumnInfo(name = "nacionalidad_id") val nacionalidadId: Int,
    @ColumnInfo(name = "parroquia_id") val parroquiaId: Int,
    @ColumnInfo(name = "domicilio") val domicilio: String,
    @ColumnInfo(name = "telefono") val telefono: String?,
    @ColumnInfo(name = "correo") val correo: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
)

fun Representante.toEntity() = RepresentanteEntity(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    cedula = cedula,
    nombres = nombres,
    apellidos = apellidos,
    fechaNacimiento = fechaNacimiento,
    genero = genero,
    etniaId = etniaId,
    nacionalidadId = nacionalidadId,
    parroquiaId = parroquiaId,
    domicilio = domicilio,
    telefono = telefono,
    correo = correo,
    updatedAt = updatedAt
)