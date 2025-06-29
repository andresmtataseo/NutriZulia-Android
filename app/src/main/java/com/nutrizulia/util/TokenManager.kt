package com.nutrizulia.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefsFilename = "auth_prefs"

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    }

    companion object {
        private const val JWT_TOKEN_KEY = "jwt_token"
    }

    fun saveToken(token: String) {
        prefs.edit {
            putString(JWT_TOKEN_KEY, token)
        }
    }

    fun getToken(): String? {
        return prefs.getString(JWT_TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit {
            remove(JWT_TOKEN_KEY)
        }
    }
}