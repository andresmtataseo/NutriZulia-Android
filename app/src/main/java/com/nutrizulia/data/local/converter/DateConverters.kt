package com.nutrizulia.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateConverters {
    // --- Convertidores para LocalDateTime (similar a MySQL DATETIME) ---
    @TypeConverter
    fun fromLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { 
            try {
                LocalDateTime.parse(it) // Parsea String ISO 8601 a LocalDateTime
            } catch (e: Exception) {
                // Si falla, intenta parsear como LocalDate y agregar tiempo por defecto
                try {
                    LocalDate.parse(it).atStartOfDay() // Convierte LocalDate a LocalDateTime con tiempo 00:00:00
                } catch (e2: Exception) {
                    null // Si ambos fallan, retorna null
                }
            }
        }
    }

    @TypeConverter
    fun toLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // Formatea LocalDateTime a String ISO 8601
    }

    // --- Convertidores para LocalDate (similar a MySQL DATE) ---
    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) } // Parsea String ISO 8601 a LocalDate
    }

    @TypeConverter
    fun toLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE) // Formatea LocalDate a String ISO 8601
    }

}