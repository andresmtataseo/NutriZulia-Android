package com.nutrizulia.data.local.enum

enum class Estado(val displayValue: String) {
    PENDIENTE("PENDIENTE"),
    REPROGRAMADA("REPROGRAMADA"),
    COMPLETADA("COMPLETADA"),
    CANCELADA("CANCELADA"),
    NO_ASISTIO("NO ASISTIÓ"),
    SIN_PREVIA_CITA("SIN PREVIA CITA")
}