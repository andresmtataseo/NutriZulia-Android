package com.nutrizulia.util

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.time.Period
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

object Utils {

    data class ZScoreResult(val zScore: Double, val percentil: Double)

    /**
     * Calcula el Z-Score y el percentil de un valor infantil según los parámetros LMS de la OMS.
     * @param medida Valor observado (peso, talla, etc.)
     * @param L Potencia Lambda (asimetría)
     * @param M Media
     * @param S Desviación estándar
     * @return Resultado con zScore y percentil
     */
    fun calcularZScoreOMS(medida: Double, L: Double, M: Double, S: Double): ZScoreResult {
        val zScore = if (L == 0.0) {
            ln(medida / M) / S
        } else {
            ((medida / M).pow(L) - 1) / (L * S)
        }

        // Cálculo del percentil a partir del z-score usando la función de distribución normal estándar (CDF)
        val percentil = zToPercentil(zScore)

        return ZScoreResult(zScore, percentil)
    }

    // Función de distribución normal acumulada (CDF) usando aproximación de error de Gauss
    fun zToPercentil(z: Double): Double {
        // fórmula de aproximación de CDF estándar para z
        val t = 1.0 / (1.0 + 0.2316419 * abs(z))
        val d = 0.3989423 * exp(-z * z / 2.0)
        val prob = d * t * (0.3193815 + t * (-0.3565638 + t * (1.781478 + t * (-1.821256 + t * 1.330274))))
        val p = if (z >= 0) 1.0 - prob else prob
        return p * 100  // convertir a percentil
    }

    fun generarUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun mostrarSnackbar(rootView: View?, mensaje: String) {
        rootView?.let {
            Snackbar.make(it, mensaje, Snackbar.LENGTH_SHORT).show()
        }
    }

    fun obtenerTexto(textInputLayout: TextInputLayout?): String {
        return textInputLayout?.editText?.text?.toString()?.trim().orEmpty()
    }

    fun mostrarErrorEnCampo(campo: TextInputLayout, mensajeError: String) {
        campo.error = mensajeError
    }

    fun calcularEdadDetallada(fechaNacimiento: LocalDate): Edad {
        val hoy = LocalDate.now()

        if (fechaNacimiento.isAfter(hoy)) {
            return Edad(0, 0, 0)
        }

        val periodo = Period.between(fechaNacimiento, hoy)
        return Edad(periodo.years, periodo.months, periodo.days)
    }

    fun calcularEdad(fechaNacimiento: LocalDate): Int {
        val hoy = LocalDate.now()
        if (fechaNacimiento.isAfter(hoy)) {
            return 0
        }
        return Period.between(fechaNacimiento, hoy).years
    }

    fun calcularEdadEnMeses(fechaNacimiento: LocalDate): Int {
        val hoy = LocalDate.now()
        if (fechaNacimiento.isAfter(hoy)) {
            return 0
        }
        val periodo = Period.between(fechaNacimiento, hoy)
        return (periodo.years * 12) + periodo.months
    }

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
            .setCancelable(isCancelable)
            .setNegativeButton(negativeButtonText) { dialog, which ->
                onNegativeClick?.invoke()
            }
            .setPositiveButton(positiveButtonText) { dialog, which ->
                onPositiveClick?.invoke()
            }
            .show()
    }

    fun mostrarAlerta(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Aceptar",
        onAcknowledge: (() -> Unit)? = null,
        isCancelable: Boolean = true
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(isCancelable)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onAcknowledge?.invoke()
                dialog.dismiss() // Cierra el diálogo al presionar el botón
            }
            .show()
    }
}
