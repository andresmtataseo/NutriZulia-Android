package com.nutrizulia.data.local.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.user.Rol

@Entity(tableName = "roles")
data class RolEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Rol.toEntity() = RolEntity(
    id = id,
    nombre = nombre
)