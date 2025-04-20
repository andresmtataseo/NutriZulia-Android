package com.nutrizulia.data.remote.service

import android.util.Log
import com.nutrizulia.data.model.EntidadModel
import com.nutrizulia.data.model.MunicipioModel
import com.nutrizulia.data.model.ParroquiaModel
import com.nutrizulia.data.remote.api.UbicacionApiClient
import javax.inject.Inject

class UbicacionService @Inject constructor(private val api: UbicacionApiClient) {

    private val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJVU2ZTQ2RXcUlVSndRc2Y2Q1BzeGExM1VrQXl3MkViSGxHX0haT3IzUWNjIn0.eyJleHAiOjE3NDUyNjU3NjQsImlhdCI6MTc0NTE4Mjk2NCwianRpIjoiYmFiMmQwMTItMGVlNi00Mjg4LTliZmYtOGE0MmZkYTcyOWVhIiwiaXNzIjoiaHR0cHM6Ly9zYWEuYXBuLmdvYi52ZS9yZWFsbXMvQVBJU0VHRU4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZjQ3ZTQzMjctMGJiZi00OWVlLWI4NDEtZDhhZWE0YjAzYjUxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXBpcyIsInNpZCI6IjEzMTA2ZWYyLWIyNDQtNDZiMS05YmEyLTk0NzhiM2Q5YzNjZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9hcGlzZWdlbi5hcG4uZ29iLnZlIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWFwaXNlZ2VuIiwib2ZmbGluZV9hY2Nlc3MiLCJhcGktaW5zdGl0dWNpb25lcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkFuZHJlcyBNb3Jlbm8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbmRyZXNtdGF0YXNlbyIsImdpdmVuX25hbWUiOiJBbmRyZXMiLCJmYW1pbHlfbmFtZSI6Ik1vcmVubyIsImVtYWlsIjoiYW5kcmVzbW9yZW5vMjAwMUBnbWFpbC5jb20ifQ.I7E1BGcVcpTEz01XRBS9ICospGKiC469LCOo414s-W8tc8tG2U_sQcUE-ALImK2lDtPTzpQP_ibH13QiSopVjE-jwRRQfVAYNFHwTxfOvrSJCMOnl6EUIv3KbQo7CSV88Hdss8jOPCAxDsaOf0bILFZ40ihXEAV9Ycik1650u-rvkwQgGWhO9W3eg77b7fzctLj2dc_Xhx89zp-to843JoasaypR6NoyI0R-tlsvyrtYNefAtaXB0X-NMF9hIy5UaQfJp-LiWRgGemB8kxPF4lnopX5YDr5V2Uvy28l1b3jACNUBhdIKHkJDtSgIZeqApaL78_RFqKFor-jL5x17eQ" // idealmente se pasa como parámetro o se obtiene desde login

    suspend fun getEntidades(): List<EntidadModel> {
        val entidadModel = api.getEntidades(token).data
        Log.d("EntidadModel", entidadModel.toString())
        Log.d("EntidadModel", entidadModel.size.toString())
        return entidadModel
    }

    suspend fun getMunicipios(codEntidad: String): List<MunicipioModel> {
        val municipioModel = api.getMunicipios(token, codEntidad).data
        Log.d("MunicipioModel", municipioModel.toString())
        Log.d("MunicipioModel", municipioModel.size.toString())
        return municipioModel
    }

    suspend fun getParroquias(codEntidad: String, codMunicipio: String): List<ParroquiaModel> {
        Log.d("Parametros", "CodEntidad: $codEntidad. CodMunicipio: $codMunicipio")
        val parroquiaModel = api.getParroquias(token, codEntidad, codMunicipio).data
        Log.d("ParroquiaModel", parroquiaModel.toString())
        Log.d("ParroquiaModel", parroquiaModel.size.toString())
        return parroquiaModel
    }

}