package com.nutrizulia.data.remote.api.sync

import com.nutrizulia.data.remote.dto.sync.SyncPullResponse
import com.nutrizulia.data.remote.dto.sync.SyncPushRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para las operaciones de sincronización bidireccional.
 * Define los endpoints para enviar cambios locales (PUSH) y recibir cambios remotos (PULL).
 */
interface ISyncService {

    /**
     * Envía los cambios locales al servidor.
     * @param request Contiene la lista de entidades modificadas localmente
     * @return Response indicando el éxito o fallo de la operación
     */
    @POST("sync/push")
    suspend fun pushChanges(@Body request: SyncPushRequest): Response<Unit>

    /**
     * Obtiene los cambios del servidor desde un timestamp específico.
     * @param since Timestamp de la última sincronización exitosa en formato ISO
     * @return Lista de entidades que han cambiado en el servidor desde el timestamp
     */
    @GET("sync/pull")
    suspend fun pullChanges(@Query("since") since: String): Response<SyncPullResponse>
}