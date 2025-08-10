package com.nutrizulia.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {

    /**
     * Verifica si hay conexión a internet disponible
     * @param context Contexto de la aplicación
     * @return true si hay conexión a internet, false en caso contrario
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnectedOrConnecting == true
        }
    }

    /**
     * Verifica si hay conexión a internet y muestra un mensaje de error si no la hay
     * @param context Contexto de la aplicación
     * @param onNoConnection Callback que se ejecuta cuando no hay conexión
     * @return true si hay conexión, false si no la hay
     */
    fun checkInternetConnectionWithCallback(
        context: Context,
        onNoConnection: () -> Unit
    ): Boolean {
        return if (isInternetAvailable(context)) {
            true
        } else {
            onNoConnection()
            false
        }
    }
}