package com.nutrizulia.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import com.nutrizulia.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.nutrizulia.data.preferences.TokenManager

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = tokenManager.getToken()

        if (!token.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment())
                .commitNow()
        }
    }
}
