package com.nutrizulia.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrizulia.databinding.ActivityPreCargarBinding
import com.nutrizulia.presentation.adapter.InstitucionAdapter
import com.nutrizulia.presentation.viewmodel.PreCargarViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreCargarActivity : AppCompatActivity() {

    private val viewModel: PreCargarViewModel by viewModels()
    private lateinit var binding: ActivityPreCargarBinding
    private lateinit var institucionAdapter: InstitucionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreCargarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinuar.visibility = View.GONE

        setupWindowInsets()
        setupRecyclerView()
        setupObservers()

        viewModel.cargarDatos()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        institucionAdapter = InstitucionAdapter(emptyList()) { perfilSeleccionado ->
            viewModel.onInstitutionSelected(perfilSeleccionado.usuarioInstitucionId)
        }

        binding.recyclerViewInstituciones.apply {
            layoutManager = LinearLayoutManager(this@PreCargarActivity)
            adapter = institucionAdapter
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.mensaje.observe(this) { mensaje ->
            if (mensaje.isNotEmpty()) {
                mostrarSnackbar(binding.root, mensaje)
            }
        }

        viewModel.profiles.observe(this) { perfiles ->
            binding.recyclerViewInstituciones.visibility =
                if (perfiles.isNotEmpty()) View.VISIBLE else View.GONE
            institucionAdapter.updatePerfilesInstitucionales(perfiles)
        }

        viewModel.continuar.observe(this) { isReadyToContinue ->
            if (isReadyToContinue) {
                navigateToMain()
            }
        }

        viewModel.authError.observe(this) { hasError ->
            if (hasError) {
                navigateToLogin()
            }
        }

        viewModel.salir.observe(this) { shouldExit ->
            if (shouldExit) {
                navigateToLogin()
            }
        }
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}