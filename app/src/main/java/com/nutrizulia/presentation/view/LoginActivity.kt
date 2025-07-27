package com.nutrizulia.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import com.nutrizulia.R
import com.nutrizulia.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)

        // 1. La verificación del token se mantiene igual.
        val token: String? = tokenManager.getToken()
        if (!token.isNullOrEmpty()) {
            navigateToMain()
            return
        }

        // 2. Simplemente se infla el layout que contiene el NavHost.
        setContentView(R.layout.activity_login)
    }

    private fun navigateToMain(): Unit {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}