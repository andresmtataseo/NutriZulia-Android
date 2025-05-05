package com.nutrizulia.domain.model

import com.nutrizulia.data.local.dto.ConsultaConPacienteYSignosVitalesDto

data class ConsultaConPacienteYSignosVitales(
    val consulta: Consulta,
    val paciente: Paciente,
    val signosVitales: SignosVitales
)

fun ConsultaConPacienteYSignosVitalesDto.toDomain(): ConsultaConPacienteYSignosVitales = ConsultaConPacienteYSignosVitales(
    consulta = Consulta(
        id = consulta.id,
        usuarioId = consulta.usuarioId,
        pacienteId = consulta.pacienteId,
        citaId = consulta.citaId,
        actividadId = consulta.actividadId,
        fecha = consulta.fecha,
        hora = consulta.hora,
        diagnosticoPrincipal = consulta.diagnosticoPrincipal,
        diagnosticoSecundario = consulta.diagnosticoSecundario,
        observaciones = consulta.observaciones
    ),paciente = Paciente(
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
        codEntidad = paciente.codEntidad,
        codMunicipio = paciente.codMunicipio,
        codParroquia = paciente.codParroquia,
        idComunidad = paciente.idComunidad,
        telefono = paciente.telefono,
        correo = paciente.correo,
        fechaIngreso = paciente.fechaIngreso
    ),
    signosVitales = SignosVitales(
        consultaId = signosVitales.consultaId,
        peso = signosVitales.peso,
        altura = signosVitales.altura,
        glicemiaBasal = signosVitales.glicemiaBasal,
        glicemiaPostprandial = signosVitales.glicemiaPostprandial,
        glicemiaAleatoria = signosVitales.glicemiaAleatoria,
        hemoglobinaGlicosilada = signosVitales.hemoglobinaGlicosilada,
        trigliceridos = signosVitales.trigliceridos,
        colesterolTotal = signosVitales.colesterolTotal,
        colesterolHdl = signosVitales.colesterolHdl,
        colesterolLdl = signosVitales.colesterolLdl,
        tensionArterial = signosVitales.tensionArterial,
        frecuenciaCardiaca = signosVitales.frecuenciaCardiaca,
        pulso = signosVitales.pulso,
        saturacionOxigeno = signosVitales.saturacionOxigeno,
        frecuenciaRespiratoria = signosVitales.frecuenciaRespiratoria,
        temperatura = signosVitales.temperatura,
        circunferenciaBraquial = signosVitales.circunferenciaBraquial,
        circunferenciaCadera = signosVitales.circunferenciaCadera,
        circunferenciaCintura = signosVitales.circunferenciaCintura,
        perimetroCefalico = signosVitales.perimetroCefalico,
        isEmbarazo = signosVitales.isEmbarazo,
        fechaUltimaMenstruacion = signosVitales.fechaUltimaMenstruacion,
        semanasGestacion = signosVitales.semanasGestacion,
        pesoPreEmbarazo = signosVitales.pesoPreEmbarazo,
        isTetero = signosVitales.isTetero,
        tipoLactancia = signosVitales.tipoLactancia
    )

)

