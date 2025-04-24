package com.nutrizulia.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.nutrizulia.R
import dagger.hilt.android.AndroidEntryPoint
import com.nutrizulia.databinding.ActivityPreCargarBinding
import com.nutrizulia.presentation.viewmodel.PreCargarViewModel

@AndroidEntryPoint
class PreCargarActivity : AppCompatActivity() {
    private val viewModel: PreCargarViewModel by viewModels()
    private lateinit var binding: ActivityPreCargarBinding
    private lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPreCargarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animationView = binding.animationView

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.textView.visibility = android.view.View.VISIBLE
                animationView.visibility = android.view.View.VISIBLE
                animationView.setAnimation(R.raw.precargar)
                animationView.loop(true)
                animationView.playAnimation()
            } else {
                binding.textView.visibility = android.view.View.INVISIBLE
                animationView.visibility = android.view.View.INVISIBLE
                animationView.cancelAnimation()
            }
        }

        viewModel.mensaje.observe(this) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                binding.textView.text = mensaje
            }
        }

        viewModel.continuar.observe(this) { continuar ->
            if (continuar) {
                binding.btnContinuar.visibility = android.view.View.VISIBLE
            }
        }

        binding.btnContinuar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        viewModel.cargarDatos()
    }
}