package com.nutrizulia.data.repository.user

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Transaction
import com.nutrizulia.data.local.dao.PerfilInstitucionalDao
import com.nutrizulia.data.local.dao.user.UsuarioInstitucionDao
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.remote.dto.user.toEntity
import javax.inject.Inject

class UsuarioInstitucionRepository @Inject constructor(
    private val api: CatalogService,
    private val usuarioInstitucionDao: UsuarioInstitucionDao,
    private val perfilInstitucionalDao: PerfilInstitucionalDao
) {

    @Transaction
    suspend fun syncUsuarioInstitucionByUsuarioId(usuarioId: Int) {
        val response = api.getUsuarioInstitucion(usuarioId)

        if (!response.isSuccessful) {
            throw Exception("Error en la respuesta del servidor: ${response.code()}")
        }

        val remoteUserInstitutions = response.body() ?: emptyList()
        val remoteEntities = remoteUserInstitutions.map { it.toEntity() }

        usuarioInstitucionDao.upsertAll(remoteEntities)

        val localUserInstitutions = usuarioInstitucionDao.findByUsuarioId(usuarioId)
        val remoteIds = remoteUserInstitutions.map { it.id }.toSet()

        val institutionsToDelete = localUserInstitutions.filter { it.id !in remoteIds }

        institutionsToDelete.forEach { institutionToDelete ->
            try {
                usuarioInstitucionDao.deleteById(institutionToDelete.id)
            } catch (e: SQLiteConstraintException) {
                Log.w("UserRepo", "No se pudo eliminar usuario_institucion_id=${institutionToDelete.id} porque está en uso.")
            }
        }
    }

    suspend fun getPerfilInstitucionalByUsuarioId(usuarioId: Int): List<PerfilInstitucional> {
        return perfilInstitucionalDao.fillAllPerfilInstitucionalByUsuarioId(usuarioId)
    }
}