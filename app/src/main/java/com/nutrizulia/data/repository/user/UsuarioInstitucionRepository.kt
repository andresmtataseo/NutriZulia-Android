package com.nutrizulia.data.repository.user

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

    suspend fun syncUsuarioInstitucionByUsuarioId(usuarioId: Int) {
        val response = api.getUsuarioInstitucion(usuarioId)
        val body = response.body() ?: throw Exception("Respuesta vacía del servidor")
        usuarioInstitucionDao.insertAll(body.map { it.toEntity() })
    }

    suspend fun getPerfilInstitucionalByUsuarioId(usuarioId: Int): List<PerfilInstitucional> {
        return perfilInstitucionalDao.fillAllPerfilInstitucionalByUsuarioId(usuarioId)
    }


}