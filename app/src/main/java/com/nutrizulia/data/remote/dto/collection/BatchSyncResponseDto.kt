package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName

data class BatchSyncResponseDto(
    @SerializedName("success") val success: List<String> = emptyList(),
    @SerializedName("failed") val failed: Map<String, String> = emptyMap()
)