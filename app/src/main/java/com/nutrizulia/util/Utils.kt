package com.nutrizulia.util

import android.annotation.SuppressLint
import android.content.Context
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Utils {

    fun generarUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }


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
     * @return El texto ingresado sin espacios extra, o cadena vacía si el campo es nulo o está vacío.
     */
    fun obtenerTexto(textInputLayout: TextInputLayout?): String {
        return textInputLayout?.editText?.text?.toString()?.trim().orEmpty()
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

    /**
     * Obtiene la hora actual en formato HH:mm.
     *
     * @return Una cadena representando la hora actual en el formato deseado.
     */
    fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm")
        return formatoHora.format(Date())
    }

    /**
     * Calcula la edad completa y la devuelve en una clase personalizada.
     * @param fechaNacimiento La fecha de nacimiento.
     * @return Un objeto `Edad` con los años, meses y días.
     */
    fun calcularEdadDetallada(fechaNacimiento: LocalDate): Edad {
        val hoy = LocalDate.now()

        if (fechaNacimiento.isAfter(hoy)) {
            return Edad(0, 0, 0)
        }

        val periodo = Period.between(fechaNacimiento, hoy)
        return Edad(periodo.years, periodo.months, periodo.days)
    }

    /**
     * Calcula la edad en años a partir de un objeto LocalDate.
     * @param fechaNacimiento La fecha de nacimiento como un objeto LocalDate.
     * @return La edad en años completos. Devuelve 0 si la fecha es en el futuro.
     */
    fun calcularEdad(fechaNacimiento: LocalDate): Int {
        val hoy = LocalDate.now()
        if (fechaNacimiento.isAfter(hoy)) {
            return 0
        }
        return Period.between(fechaNacimiento, hoy).years
    }

    /**
     * Muestra un diálogo de alerta genérico de Material Design 3 con dos botones (positivo y negativo).
     *
     * @param context El Context necesario para crear el diálogo.
     * @param title El título del diálogo. Se recomienda usar getString(R.string.your_title) para localización.
     * @param message El mensaje principal del diálogo. Se recomienda usar getString(R.string.your_message).
     * @param positiveButtonText El texto para el botón positivo (por defecto: "Aceptar"). Usa recursos de String.
     * @param negativeButtonText El texto para el botón negativo (por defecto: "Cancelar"). Usa recursos de String.
     * @param onPositiveClick Lambda (función) opcional que se ejecuta al presionar el botón positivo.
     * @param onNegativeClick Lambda (función) opcional que se ejecuta al presionar el botón negativo.
     * @param isCancelable Boolean que indica si el diálogo se puede cerrar tocando fuera de él o con el botón Atrás (por defecto: true).
     */
    fun mostrarDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Aceptar", // Valor por defecto
        negativeButtonText: String = "Cancelar", // Valor por defecto
        onPositiveClick: (() -> Unit)? = null, // Acción opcional para el botón positivo
        onNegativeClick: (() -> Unit)? = null, // Acción opcional para el botón negativo
        isCancelable: Boolean = true // Por defecto, se puede cancelar
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(isCancelable) // Establecer si se puede cancelar o no
            .setNegativeButton(negativeButtonText) { dialog, which ->
                // Ejecutar la acción negativa si se proporcionó
                onNegativeClick?.invoke()
                // El diálogo se cierra automáticamente por defecto al hacer clic en un botón
            }
            .setPositiveButton(positiveButtonText) { dialog, which ->
                // Ejecutar la acción positiva si se proporcionó
                onPositiveClick?.invoke()
                // El diálogo se cierra automáticamente
            }
            .show() // Mostrar el diálogo
    }
}
