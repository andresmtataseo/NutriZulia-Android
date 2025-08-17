package com.nutrizulia.domain.exception

/**
 * Excepciones específicas para el manejo del historial del paciente
 */
sealed class HistorialPacienteException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * Error al cargar los datos del paciente
     */
    class PacienteNoEncontradoException(pacienteId: String, cause: Throwable? = null) : 
        HistorialPacienteException("No se pudo encontrar el paciente con ID: $pacienteId", cause)
    
    /**
     * Error al cargar el historial médico
     */
    class HistorialNoDisponibleException(pacienteId: String, cause: Throwable? = null) : 
        HistorialPacienteException("No se pudo cargar el historial del paciente: $pacienteId", cause)
    
    /**
     * Error al cargar las especialidades
     */
    class EspecialidadesNoDisponiblesException(pacienteId: String, cause: Throwable? = null) : 
        HistorialPacienteException("No se pudieron cargar las especialidades del paciente: $pacienteId", cause)
    
    /**
     * Error al cargar los años del historial
     */
    class AnosHistorialNoDisponiblesException(pacienteId: String, cause: Throwable? = null) : 
        HistorialPacienteException("No se pudieron cargar los años del historial del paciente: $pacienteId", cause)
    
    /**
     * Error de conexión o red
     */
    class ConexionException(message: String = "Error de conexión", cause: Throwable? = null) : 
        HistorialPacienteException(message, cause)
    
    /**
     * Error de autenticación o sesión
     */
    class SesionException(message: String = "Error de sesión", cause: Throwable? = null) : 
        HistorialPacienteException(message, cause)
    
    /**
     * Error genérico del sistema
     */
    class SistemaException(message: String = "Error del sistema", cause: Throwable? = null) : 
        HistorialPacienteException(message, cause)
}