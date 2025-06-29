package com.nutrizulia.domain.model.auth

import com.nutrizulia.data.remote.dto.auth.SignInResponseDto
import com.nutrizulia.data.remote.dto.user.toDomain
import com.nutrizulia.domain.model.user.Usuario
import com.nutrizulia.domain.model.user.UsuarioInstitucion

data class SignIn(
    val token: String,
    val type: String,
    val usuario: Usuario
)

fun SignInResponseDto.toDomain() = SignIn(
    token = token,
    type = type,
    usuario = user.toDomain()
)