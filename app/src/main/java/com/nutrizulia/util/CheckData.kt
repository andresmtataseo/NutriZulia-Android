package com.nutrizulia.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CheckData {

    /**
     * Verifica si una cédula venezolana es válida.
     *
     * @param cedula La cédula a validar.
     * @return true si la cédula cumple con el formato, false en caso contrario.
     */
    fun esCedulaValida(cedula: String?): Boolean {
        if (cedula.isNullOrEmpty()) {
            return false
        }
        val patronCedula = "^(?i)[VE]-\\d{1,8}(-\\d{2})?$"
        return cedula.matches(Regex(patronCedula))
    }

    /**
     * Verifica si un correo electrónico tiene un formato válido.
     *
     * @param email El correo electrónico a verificar.
     * @return true si el correo tiene un formato válido, false en caso contrario.
     */
    fun esCorreoValido(email: String?): Boolean {
        val patronCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email?.matches(Regex(patronCorreo)) == true
    }

    /**
     * Valida si una cadena es un número de teléfono venezolano válido con formato:
     * Prefijo (0414, 0424, 0412, 0416, 0426) + 7 dígitos.
     *
     * @param numero La cadena a validar (puede ser null).
     * @return true si el número coincide con el patrón especificado, false en caso contrario.
     */
    fun esNumeroTelefonoValido(numero: String?): Boolean {
        if (numero.isNullOrEmpty()) {
            return false
        }
        val patronTelefono = "^(0414|0424|0412|0416|0426)\\d{7}$"
        return numero.matches(Regex(patronTelefono))
    }

    /**
     * Verifica si una fecha tiene un formato válido (DD/MM/YYYY) y es real en el calendario.
     *
     * @param fecha La fecha a validar.
     * @return true si la fecha cumple con el formato y es una fecha válida en el calendario.
     */
    fun esFechaValida(fecha: String?): Boolean {
        val patronFecha = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$"
        if (fecha == null || !fecha.matches(Regex(patronFecha))) return false

        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.isLenient = false
            val date: Date? = formato.parse(fecha)
            date != null
        } catch (e: Exception) {
            false
        }
    }
}
