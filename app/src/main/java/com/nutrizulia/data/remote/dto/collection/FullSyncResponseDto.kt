package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de sincronización completa del backend
 * Estructura esperada:
 * {
 *   "tabla": "pacientes",
 *   "totalRegistros": 150,
 *   "datos": [...]
 * }
 */
data class FullSyncResponseDto<T>(
    @SerializedName("tabla") val tabla: String,
    @SerializedName("totalRegistros") val totalRegistros: Int,
    @SerializedName("datos") val datos: List<T>
)