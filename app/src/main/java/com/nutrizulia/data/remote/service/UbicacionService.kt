package com.nutrizulia.data.remote.service

import android.util.Log
import com.nutrizulia.data.model.EntidadModel
import com.nutrizulia.data.model.MunicipioModel
import com.nutrizulia.data.model.ParroquiaModel
import com.nutrizulia.data.remote.api.UbicacionApiClient
import javax.inject.Inject

class UbicacionService @Inject constructor(private val api: UbicacionApiClient) {

    private val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJVU2ZTQ2RXcUlVSndRc2Y2Q1BzeGExM1VrQXl3MkViSGxHX0haT3IzUWNjIn0.eyJleHAiOjE3NDUxNjU3ODYsImlhdCI6MTc0NTA4Mjk4NiwianRpIjoiYzUyNmNiOTItYTg0OC00OTdiLTkzNDEtMjZjNjliYzYyNjBjIiwiaXNzIjoiaHR0cHM6Ly9zYWEuYXBuLmdvYi52ZS9yZWFsbXMvQVBJU0VHRU4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZjQ3ZTQzMjctMGJiZi00OWVlLWI4NDEtZDhhZWE0YjAzYjUxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXBpcyIsInNpZCI6Ijc3YTJiMzU3LWU4NjAtNGQ2YS1hNjA3LTFhZjdkMjVhMTc4YSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9hcGlzZWdlbi5hcG4uZ29iLnZlIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWFwaXNlZ2VuIiwib2ZmbGluZV9hY2Nlc3MiLCJhcGktaW5zdGl0dWNpb25lcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkFuZHJlcyBNb3Jlbm8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbmRyZXNtdGF0YXNlbyIsImdpdmVuX25hbWUiOiJBbmRyZXMiLCJmYW1pbHlfbmFtZSI6Ik1vcmVubyIsImVtYWlsIjoiYW5kcmVzbW9yZW5vMjAwMUBnbWFpbC5jb20ifQ.LZdfwZ809LaK_1je0_df9a3_Yrm7VpYMk7b7buAdGXS318gr8QlnDYaZTs3xlXJibxGolh7SwQZuz4H2Nz28ItU93Xsr-7xEhBem5ZrJJtIZ_b1XI8f33B0Sy4b6UkZobnRbdq_7lLpy8O1RYyaRXbxAk-dWzDM7cjjmBBLejCH5vr1KZRkQsBlbYvTUC7U3bnlBA8vxVHeby-FH-VfUX8RucOgS-wofOw3jX4VWr51cmRRDcyrl5nsmXT7dZ-sGCKMGDLBpwErRWYWGPy9xeHA91McrE7tAnhJthfMI1fdgFwmtJTuwgxdtNEP80VYEPLR7q3gnNats2DQOD8DPLA" // idealmente se pasa como parámetro o se obtiene desde login

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