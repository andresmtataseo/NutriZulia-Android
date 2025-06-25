package com.nutrizulia.domain.usecase.collection

//import com.nutrizulia.domain.model.Paciente
//import com.nutrizulia.domain.exception.* // Importa tus excepciones personalizadas
//import javax.inject.Inject
//
//class InsertPacienteUseCase @Inject constructor(private val repository: PacienteRepository) {
//
//    suspend operator fun invoke(paciente: Paciente): Long {
//
//        val comprobarCedula = repository.getPacienteByCedula(paciente.cedula)
//        if (comprobarCedula != null) {
//            throw DuplicateCédulaException()
//        }
//
//        if (paciente.correo.isNullOrEmpty().not()) {
//            val comprobarCorreo = paciente.correo?.let { repository.getPacienteByCorreo(it) }
//            if (comprobarCorreo != null) {
//                throw DuplicateCorreoException()
//            }
//        }
//
//        if (paciente.telefono.isNullOrEmpty().not()) {
//            val comprobarTelefono = paciente.telefono?.let { repository.getPacienteByTelefono(it) }
//            if (comprobarTelefono != null) {
//                throw DuplicateTelefonoException()
//            }
//        }
//
//        return repository.insertPaciente(paciente)
//    }
//}