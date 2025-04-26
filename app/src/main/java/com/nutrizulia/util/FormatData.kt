package com.nutrizulia.util

object FormatData {

    /**
     * Estandariza la parte numérica principal de una cédula venezolana rellenando con ceros
     * a la izquierda hasta alcanzar 8 dígitos.
     * ESTA FUNCIÓN ASUME que la cadena de entrada YA TIENE UNA ESTRUCTURA VÁLIDA
     * (es decir, ha pasado la validación con esCedulaValidaEstructura).
     * Si la estructura de entrada no es válida, el comportamiento es indefinido
     * (probablemente lanzará una excepción o devolverá un resultado incorrecto).
     *
     * @param cedulaConEstructuraValida La cadena de cédula con estructura válida (no debe ser null o vacía).
     * @return La cédula estandarizada con la parte numérica principal rellena a 8 dígitos.
     */
    fun formatearCedula(cedulaConEstructuraValida: String): String {
        val regexConCapturas = Regex("^(?i)([VE])-(\\d{1,8})(-\\d{2})?$")
        val matchResult = regexConCapturas.matchEntire(cedulaConEstructuraValida)!!
        val (prefix, number, suffix) = matchResult.destructured
        val paddedNumber = number.padStart(8, '0')
        return "${prefix.uppercase()}-$paddedNumber$suffix"
    }

    /**
     * Formatea un número de teléfono venezolano móvil (04XX) añadiendo un guion (-)
     * después del prefijo 04XX si aún no lo tiene, o lo mantiene si ya está presente.
     * Solo formatea números que coinciden con un prefijo 04XX válido seguido de exactamente 7 dígitos.
     *
     * @param numero La cadena del número de teléfono a formatear (puede ser null).
     * @return El número de teléfono formateado como "04XX-XXXXXXX" si el formato de entrada es válido,
     * o null si la entrada no es un número de teléfono 04XX válido para formatear.
     */
    fun formatearTelefono(numero: String?): String? {
        if (numero.isNullOrEmpty()) return null

        val regexFormato = Regex("^(0414|0424|0412|0416|0426)-?(\\d{7})$")
        val matchResult = regexFormato.matchEntire(numero) ?: return null

        val (prefix, digits) = matchResult.destructured
        return "$prefix-$digits"
    }


}
