package com.nutrizulia.domain.model

import com.nutrizulia.data.local.dto.ParroquiaDto

data class Parroquia(
    val id: Int,
    val codParroquia: String,
    val parroquia: String
)

fun ParroquiaDto.toDomain() = Parroquia(
    id = id,
    codParroquia = codParroquia,
    parroquia = parroquia
)