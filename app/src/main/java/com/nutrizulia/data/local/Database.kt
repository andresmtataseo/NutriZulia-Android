package com.nutrizulia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nutrizulia.data.local.dao.*
import com.nutrizulia.data.local.entity.*


@Database(
    entities = [
        PacienteEntity::class,
        ComunidadEntity::class,
        ParroquiaEntity::class,
        MunicipioEntity::class,
        EntidadEntity::class,
        RepresentanteEntity::class,
        CitaEntity::class,
        ConsultaEntity::class,
        SignosVitalesEntity::class,
        UsuarioEntity::class,
        ActividadEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class Database: RoomDatabase() {

    abstract fun getPacienteDao(): PacienteDao
    abstract fun getComunidadDao(): ComunidadDao
    abstract fun getParroquiaDao(): ParroquiaDao
    abstract fun getMunicipioDao(): MunicipioDao
    abstract fun getEntidadDao(): EntidadDao
    abstract fun getRepresentanteDao(): RepresentanteDao
    abstract fun getCitaDao(): CitaDao
    abstract fun getConsultaDao(): ConsultaDao
    abstract fun getSignosVitalesDao(): SignosVitalesDao
    abstract fun getUsuarioDao(): UsuarioDao
    abstract fun getActividadDao(): ActividadDao

}