package com.nutrizulia.util

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date

object Utils {

    /**
     * Muestra un mensaje tipo Snackbar en la pantalla.
     *
     * @param rootView Vista raíz en la cual se mostrará el Snackbar.
     * @param mensaje Texto del mensaje que aparecerá en el Snackbar.
     */
    fun mostrarSnackbar(rootView: View?, mensaje: String) {
        rootView?.let {
            Snackbar.make(it, mensaje, Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtiene el texto ingresado en un campo TextInputLayout.
     *
     * @param textInputLayout Componente TextInputLayout del cual se extraerá el texto.
     * @return El texto ingresado sin espacios extra, o `null` si el campo es nulo o está vacío.
     */
    fun obtenerTexto(textInputLayout: TextInputLayout?): String {
        val texto = textInputLayout?.editText?.text?.toString()?.trim()
        return if (texto.isNullOrEmpty()) "" else texto
    }

    /**
     * Establece un mensaje de error en un TextInputLayout.
     *
     * @param campo Campo de entrada donde se mostrará el mensaje de error.
     * @param mensajeError Texto del error que se mostrará en el campo.
     */
    fun mostrarErrorEnCampo(campo: TextInputLayout, mensajeError: String) {
        campo.error = mensajeError
    }

    /**
     * Obtiene la fecha actual en formato DD/MM/YYYY.
     *
     * @return Una cadena representando la fecha actual en el formato deseado.
     */
    @SuppressLint("SimpleDateFormat")
    fun obtenerFechaActual(): String {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy")
        return formatoFecha.format(Date())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularEdad(fechaNacimiento: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val nacimiento = LocalDate.parse(fechaNacimiento, formatter)
            val hoy = LocalDate.now()
            Period.between(nacimiento, hoy).years
        } catch (e: Exception) {
            -1
        }
    }
}
