package com.nutrizulia.util

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtUtils {


    fun extractIdUsuario(token: String?): Int? {
        if (token.isNullOrBlank()) {
            Log.w("JwtUtils", "El token proporcionado es nulo o está vacío.")
            return null
        }

        try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("JwtUtils", "El token no tiene el formato JWT correcto.")
                return null
            }

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            val jsonObject = JSONObject(decodedString)

            return if (jsonObject.has("idUsuario")) {
                jsonObject.getInt("idUsuario")
            } else {
                Log.w("JwtUtils", "El claim 'idUsuario' no fue encontrado en el token.")
                null
            }

        } catch (e: Exception) {
            Log.e("JwtUtils", "Error al decodificar o parsear el JWT: ${e.message}")
            return null
        }
    }
}