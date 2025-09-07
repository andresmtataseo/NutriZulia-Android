package com.nutrizulia.util

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class DateRangePickerUtil {

    companion object {
        private const val DATE_FORMAT = "dd/MM/yyyy"
        private const val DATE_PICKER_TAG = "DATE_RANGE_PICKER"

        /**
         * Muestra un diálogo de selección de rango de fechas
         * @param fragmentManager FragmentManager para mostrar el diálogo
         * @param onDateRangeSelected Callback que se ejecuta cuando se selecciona un rango
         */
        fun showDateRangePicker(
            fragmentManager: FragmentManager,
            onDateRangeSelected: (startDate: Long, endDate: Long, formattedRange: String) -> Unit
        ) {
            // Configurar restricciones del calendario
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now()) // Solo fechas hasta hoy

            // Crear el picker de rango de fechas
            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Seleccionar rango de fechas")
                .setCalendarConstraints(constraintsBuilder.build())
                .setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar)
                .build()

            // Configurar listener para cuando se selecciona el rango
            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                selection?.let { dateRange ->
                    val startDate = dateRange.first
                    val endDate = dateRange.second
                    
                    if (startDate != null && endDate != null) {
                        val formattedRange = formatDateRange(startDate, endDate)
                        onDateRangeSelected(startDate, endDate, formattedRange)
                    }
                }
            }

            // Mostrar el diálogo
            dateRangePicker.show(fragmentManager, DATE_PICKER_TAG)
        }

        /**
         * Formatea un rango de fechas para mostrar en la UI
         * @param startDate Fecha de inicio en milisegundos
         * @param endDate Fecha de fin en milisegundos
         * @return String formateado del rango de fechas
         */
        fun formatDateRange(startDate: Long, endDate: Long): String {
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val startFormatted = dateFormat.format(Date(startDate))
            val endFormatted = dateFormat.format(Date(endDate))
            
            return if (startFormatted == endFormatted) {
                startFormatted // Si es el mismo día, mostrar solo una fecha
            } else {
                "$startFormatted - $endFormatted"
            }
        }

        /**
         * Valida si un rango de fechas es válido
         * @param startDate Fecha de inicio en milisegundos
         * @param endDate Fecha de fin en milisegundos
         * @return true si el rango es válido, false en caso contrario
         */
        fun isValidDateRange(startDate: Long, endDate: Long): Boolean {
            return startDate <= endDate && endDate <= System.currentTimeMillis()
        }

        /**
         * Convierte una fecha en milisegundos a formato de fecha para consultas SQL
         * @param timestamp Fecha en milisegundos
         * @return String en formato yyyy-MM-dd
         */
        fun formatDateForQuery(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

        /**
         * Obtiene el inicio del día para una fecha dada
         * @param timestamp Fecha en milisegundos
         * @return Timestamp del inicio del día
         */
        fun getStartOfDay(timestamp: Long): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

        /**
         * Obtiene el final del día para una fecha dada
         * @param timestamp Fecha en milisegundos
         * @return Timestamp del final del día
         */
        fun getEndOfDay(timestamp: Long): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.timeInMillis
        }
    }
}