package com.nutrizulia.util

object FormatData {

    /**
     * Convierte un nombre a mayúsculas.
     *
     * @param nombre o apellido El nombre a formatear.
     * @return El nombre o apellido en mayúsculas, o una cadena vacía si es nulo.
     */
    fun formatearNombre(nombre: String?): String {
        return if (nombre.isNullOrEmpty()) {
            ""
        } else {
            nombre.uppercase()
        }
    }

    /**
     * Convierte un correo electrónico a minúsculas.
     *
     * @param correo El correo electrónico a formatear.
     * @return El correo en minúsculas, o una cadena vacía si es nulo o vacío.
     */
    fun formatearCorreo(correo: String): String {
        return if (correo.isNullOrEmpty()) {
            ""
        } else {
            correo.trim().lowercase()
        }
    }
}
