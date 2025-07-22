package com.nutrizulia.domain.usecase.catalog

import com.nutrizulia.data.repository.catalog.GrupoEtarioRepository
import com.nutrizulia.domain.model.catalog.GrupoEtario
import javax.inject.Inject

class GetGrupoEtario @Inject constructor(
    private val repository: GrupoEtarioRepository
) {
    suspend operator fun invoke(edadMes: Int): GrupoEtario? {
        return repository.findByEdad(edadMes)
    }
}