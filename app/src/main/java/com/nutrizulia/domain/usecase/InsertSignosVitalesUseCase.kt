package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.SignosVitalesRepository
import com.nutrizulia.domain.model.SignosVitales
import javax.inject.Inject

class InsertSignosVitalesUseCase @Inject constructor(
    private val signosVitalesRepository: SignosVitalesRepository
) {

    suspend operator fun invoke(signosVitales: SignosVitales): Long {
        return signosVitalesRepository.insertSignosVitales(signosVitales)
    }

}