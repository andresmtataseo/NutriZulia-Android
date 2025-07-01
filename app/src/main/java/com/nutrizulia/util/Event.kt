package com.nutrizulia.util
/**
 * Wrapper para datos que se exponen a través de LiveData y que solo deben consumirse una vez,
 * como la navegación o un Snackbar.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Solo se puede modificar dentro de la clase

    /**
     * Retorna el contenido y previene que se use de nuevo.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Retorna el contenido, incluso si ya fue manejado.
     */
    fun peekContent(): T = content
}