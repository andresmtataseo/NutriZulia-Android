package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de sincronización por lotes del servidor.
 * El servidor devuelve una lista de UUIDs exitosos y un mapa de UUIDs fallidos con sus motivos.
 */
data class BatchSyncResponseDto(
    @SerializedName("success")
    val success: List<String> = emptyList(), // Lista de UUIDs sincronizados correctamente
    
    @SerializedName("failed")
    val failed: Map<String, String> = emptyMap() // Mapa de UUID -> motivo del fallo
)