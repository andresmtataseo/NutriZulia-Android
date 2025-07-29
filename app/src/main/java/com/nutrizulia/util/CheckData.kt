package com.nutrizulia.util

object CheckData {

    fun esCedulaValida(cedula: String?): Boolean {
        if (cedula.isNullOrEmpty()) {
            return false
        }
        // Patrón para cédulas normales: V-12345678 o E-12345678 (sin sufijo temporal)
        val patronCedulaNormal = "^(?i)[VE]-\\d{1,8}$"
        // Patrón para cédulas temporales: V-12345678-01 (con sufijo temporal de 2 dígitos)
        val patronCedulaTemporal = "^(?i)[VE]-\\d{1,8}-\\d{2}$"
        
        return cedula.matches(Regex(patronCedulaNormal)) || cedula.matches(Regex(patronCedulaTemporal))
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
