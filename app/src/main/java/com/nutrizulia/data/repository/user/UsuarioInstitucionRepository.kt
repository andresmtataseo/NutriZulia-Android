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

        // 1. Inserta nuevos registros y actualiza los existentes sin borrar nada.
        usuarioInstitucionDao.upsertAll(remoteEntities)

        // 2. Manejo seguro de eliminaciones
        val localUserInstitutions = usuarioInstitucionDao.findByUsuarioId(usuarioId)
        val remoteIds = remoteUserInstitutions.map { it.id }.toSet()

        val institutionsToDelete = localUserInstitutions.filter { it.id !in remoteIds }

        institutionsToDelete.forEach { institutionToDelete ->
            try {
                // 3. Intenta borrar cada registro obsoleto individualmente.
                usuarioInstitucionDao.deleteById(institutionToDelete.id)
            } catch (e: SQLiteConstraintException) {
                // 4. Si falla, es porque está en uso. Lo ignoramos y continuamos.
                // Aquí puedes loguear que no se pudo borrar un registro en desuso.
                Log.w("UserRepo", "No se pudo eliminar usuario_institucion_id=${institutionToDelete.id} porque está en uso.")
            }
        }
    }

    suspend fun getPerfilInstitucionalByUsuarioId(usuarioId: Int): List<PerfilInstitucional> {
        return perfilInstitucionalDao.fillAllPerfilInstitucionalByUsuarioId(usuarioId)
    }
}