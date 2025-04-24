package com.nutrizulia.domain.exception

open class DomainException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class DuplicateCédulaException(message: String = "Ya existe un paciente con esa cédula.") : DomainException(message)
class DuplicateCorreoException(message: String = "Ya existe un paciente con ese correo.") : DomainException(message)
class DuplicateTelefonoException(message: String = "Ya existe un paciente con ese teléfono.") : DomainException(message)