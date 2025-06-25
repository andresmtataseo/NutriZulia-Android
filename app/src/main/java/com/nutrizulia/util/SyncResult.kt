package com.nutrizulia.util

data class SyncResult(
    val totalInsertados: Int,
    val success: Boolean,
    val message: String
)