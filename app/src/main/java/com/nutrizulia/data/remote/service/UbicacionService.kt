package com.nutrizulia.data.remote.service

import android.util.Log
import com.nutrizulia.data.model.EntidadModel
import com.nutrizulia.data.model.MunicipioModel
import com.nutrizulia.data.model.ParroquiaModel
import com.nutrizulia.data.remote.api.UbicacionApiClient
import javax.inject.Inject

class UbicacionService @Inject constructor(private val api: UbicacionApiClient) {

    private val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJVU2ZTQ2RXcUlVSndRc2Y2Q1BzeGExM1VrQXl3MkViSGxHX0haT3IzUWNjIn0.eyJleHAiOjE3NDUzNjU4NjEsImlhdCI6MTc0NTI4MzA2MSwianRpIjoiZmQxMjY2YTItZDc3Zi00NTk3LThjNzMtMmUyZmI3ODE0N2E5IiwiaXNzIjoiaHR0cHM6Ly9zYWEuYXBuLmdvYi52ZS9yZWFsbXMvQVBJU0VHRU4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZjQ3ZTQzMjctMGJiZi00OWVlLWI4NDEtZDhhZWE0YjAzYjUxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXBpcyIsInNpZCI6IjBjMDNhNmUwLTQzYzgtNGViZi1hZjk3LWQ5NDcwYzcyOWE3NCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9hcGlzZWdlbi5hcG4uZ29iLnZlIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWFwaXNlZ2VuIiwib2ZmbGluZV9hY2Nlc3MiLCJhcGktaW5zdGl0dWNpb25lcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkFuZHJlcyBNb3Jlbm8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbmRyZXNtdGF0YXNlbyIsImdpdmVuX25hbWUiOiJBbmRyZXMiLCJmYW1pbHlfbmFtZSI6Ik1vcmVubyIsImVtYWlsIjoiYW5kcmVzbW9yZW5vMjAwMUBnbWFpbC5jb20ifQ.N15uR2CbtnNtqO6eHPXdtB2rH-BpYnF1jwPVBZKKfw5S7Vbr4j8hS25wpAIWb2YSxGh43BMTSAM8JftaMCvIMKpg28-TvOYKEAg9h4hlxLmd-cWlbCq7LNX06I6UGIXslhDWizIc-fF7xHsrxST9xfSggsWniv3sn4FnTnGW24A-BDqa1KHmMIIow9g9op_ierWKE3z1JVxUztm6nfvnNuL8YQEZQzUunwlgGgV3fnuS24YOW2E9Tn3ub3NISaNxrQsLrTXo7kC5AixtiXHqbw6ELhcWGXEUugvut9LZD-qFRpx92gZ5oeEWq_DJgrzKVkVpKKFD07nLuT02Ce7Taw"

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