package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nutrizulia.data.local.entity.PacienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes ORDER BY fecha_ingreso ASC")
    suspend fun getPacientes(): List<PacienteEntity>

    @Query("""
    SELECT * FROM pacientes 
    WHERE 
        primer_nombre LIKE '%' || :filtro || '%' OR 
        segundo_nombre LIKE '%' || :filtro || '%' OR 
        primer_apellido LIKE '%' || :filtro || '%' OR 
        segundo_apellido LIKE '%' || :filtro || '%' OR 
        cedula LIKE '%' || :filtro || '%' OR 
        nacionalidad LIKE '%' || :filtro || '%' OR 
        genero LIKE '%' || :filtro || '%' OR 
        etnia LIKE '%' || :filtro || '%' OR
        telefono LIKE '%' || :filtro || '%' OR 
        correo LIKE '%' || :filtro || '%'
""")
    fun getPacientesByFiltro(filtro: String): Flow<List<PacienteEntity>>

    @Query("SELECT * FROM pacientes WHERE cedula = :cedula")
    suspend fun getPacienteByCedula(cedula: String): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE correo = :correo")
    suspend fun getPacienteByCorreo(correo: String): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE telefono = :telefono")
    suspend fun getPacienteByTelefono(telefono: String): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE id = :idPaciente")
    suspend fun getPacienteById(idPaciente: Int): PacienteEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPaciente(paciente: PacienteEntity): Long

    @Update
    suspend fun updatePaciente(paciente: PacienteEntity): Int

    @Query("DELETE FROM pacientes WHERE cedula = :cedula")
    suspend fun deletePacienteByCedula(cedula: String)

}