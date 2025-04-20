package com.nutrizulia.util

enum class EstadoCita(val descripcion: String) {
    PENDIENTE("PENDIENTE"),
    COMPLETADA("COMPLETADA"),
    CANCELADA("CANCELADA"),
    REPROGRAMADA("REPROGRAMDA"),
    NO_ASISTIO("NO ASISTIÓ");
}