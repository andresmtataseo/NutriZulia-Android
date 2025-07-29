package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.PacienteRepresentanteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class PacienteRepresentanteRepository @Inject constructor(
    private val pacienteRepresentanteDao: PacienteRepresentanteDao
){
    suspend fun countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId: Int, representanteId: String) : Int {
        return pacienteRepresentanteDao.countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId, representanteId)
    }
    
    suspend fun findByPacienteId(pacienteId: String): PacienteRepresentante? {
        return pacienteRepresentanteDao.findByPacienteId(pacienteId)?.toDomain()
    }
    
    suspend fun upsert(pacienteRepresentante: PacienteRepresentante) {
        return pacienteRepresentanteDao.upsert(pacienteRepresentante.toEntity())
    }
}