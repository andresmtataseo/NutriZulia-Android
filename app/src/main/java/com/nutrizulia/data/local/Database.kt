package com.nutrizulia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nutrizulia.data.local.dao.*
import com.nutrizulia.data.local.entity.*


@Database(
    entities = [
        PacienteEntity::class,
        UbicacionEntity::class,
        RepresentanteEntity::class,
        CitaEntity::class,
        ConsultaEntity::class,
        SignosVitalesEntity::class,
        UsuarioEntity::class,
        ActividadEntity::class
    ],
    version = 6
)
abstract class Database: RoomDatabase() {

    abstract fun getPacienteDao(): PacienteDao
    abstract fun getUbicacionDao(): UbicacionDao
    abstract fun getRepresentanteDao(): RepresentanteDao
    abstract fun getCitaDao(): CitaDao
    abstract fun getConsultaDao(): ConsultaDao
    abstract fun getSignosVitalesDao(): SignosVitalesDao
    abstract fun getUsuarioDao(): UsuarioDao
    abstract fun getActividadDao(): ActividadDao

}