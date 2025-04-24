package com.nutrizulia.data.model

data class UbicacionModel(
    val codEntidad: Int,
    val entidad: String,
    val codMunicipio: Int,
    val municipio: String,
    val codParroquia: Int,
    val parroquia: String,
    val idComunidad: Int,
    val nombreComunidad: String
)
