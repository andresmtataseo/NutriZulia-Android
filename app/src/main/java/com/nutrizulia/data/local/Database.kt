package com.nutrizulia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nutrizulia.data.local.entity.*
import com.nutrizulia.data.local.dao.PacienteDao

@Database(
    entities = [
        PacienteEntity::class,
        ParroquiaEntity::class,
        MunicipioEntity::class,
        EstadoEntity::class
    ],
    version = 1
)
abstract class Database: RoomDatabase() {

    abstract fun getPacienteDao(): PacienteDao

}