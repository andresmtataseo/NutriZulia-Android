package com.nutrizulia.data.local.converter

import androidx.room.TypeConverter
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.data.local.enum.TipoLactancia
import com.nutrizulia.data.local.enum.TipoValorCalculado

class EnumConverters {

    // --- Convertidores para Enum Estado ---
    @TypeConverter
    fun fromEstado(value: String?): Estado? {
        return value?.let { Estado.valueOf(it) }
    }

    @TypeConverter
    fun toEstado(estado: Estado?): String? {
        return estado?.name
    }

    // --- Convertidores para Enum TipoConsulta ---
    @TypeConverter
    fun fromTipoConsulta(value: String?): TipoConsulta? {
        return value?.let { TipoConsulta.valueOf(it) }
    }

    @TypeConverter
    fun toTipoConsulta(tipoConsulta: TipoConsulta?): String? {
        return tipoConsulta?.name
    }

    // --- Convertidores para Enum TipoLactancia ---
    @TypeConverter
    fun fromTipoLactancia(value: String?): TipoLactancia? {
        return value?.let { TipoLactancia.valueOf(it) }
    }

    @TypeConverter
    fun toTipoLactancia(tipoLactancia: TipoLactancia?): String? {
        return tipoLactancia?.name
    }

    // --- Convertidores para Enum TipoValorCalculado ---
    @TypeConverter
    fun fromTipoValorCalculado(value: String?): TipoValorCalculado? {
        return value?.let { TipoValorCalculado.valueOf(it) }
    }

    @TypeConverter
    fun toTipoValorCalculado(tipoValorCalculado: TipoValorCalculado?): String? {
        return tipoValorCalculado?.name
    }
}