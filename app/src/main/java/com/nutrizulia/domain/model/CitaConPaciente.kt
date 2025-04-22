package com.nutrizulia.domain.model

import com.nutrizulia.data.local.dto.CitaConPacienteDto

class CitaConPaciente (
    val cita: Cita,
    val paciente: Paciente
)

fun CitaConPacienteDto.toDomain(): CitaConPaciente = CitaConPaciente(
    cita = Cita(
        id = cita.id,
        usuarioId = cita.usuarioId,
        pacienteId = cita.pacienteId,
        tipoCita = cita.tipoCita,
        especialidad = cita.especialidad,
        motivoCita = cita.motivoCita,
        fechaProgramada = cita.fechaProgramada,
        horaProgramada = cita.horaProgramada,
        estado = cita.estado
    ),
    paciente = Paciente(
        id = paciente.id,
        cedula = paciente.cedula,
        primerNombre = paciente.primerNombre,
        segundoNombre = paciente.segundoNombre,
        primerApellido = paciente.primerApellido,
        segundoApellido = paciente.segundoApellido,
        fechaNacimiento = paciente.fechaNacimiento,
        genero = paciente.genero,
        etnia = paciente.etnia,
        nacionalidad = paciente.nacionalidad,
        grupoSanguineo = paciente.grupoSanguineo,
        ubicacionId = paciente.ubicacionId,
        telefono = paciente.telefono,
        correo = paciente.correo,
        fechaIngreso = paciente.fechaIngreso
    )
)
