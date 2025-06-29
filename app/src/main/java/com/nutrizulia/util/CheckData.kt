package com.nutrizulia.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

object CheckData {

    fun esCedulaValida(cedula: String?): Boolean {
        if (cedula.isNullOrEmpty()) {
            return false
        }
        val patronCedula = "^(?i)[VE]-\\d{1,8}(-\\d{2})?$"
        return cedula.matches(Regex(patronCedula))
    }

    fun esCorreoValido(email: String?): Boolean {
        val patronCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email?.matches(Regex(patronCorreo)) == true
    }

    fun esNumeroTelefonoValido(numero: String?): Boolean {
        if (numero.isNullOrEmpty()) {
            return false
        }
        val patronTelefono = "^(0414|0424|0412|0416|0426)\\d{7}$"
        return numero.matches(Regex(patronTelefono))
    }

}
