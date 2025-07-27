package com.nutrizulia.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.nutrizulia.R
import com.nutrizulia.databinding.ActivityMainBinding
import com.nutrizulia.presentation.viewmodel.MainViewModel
import com.nutrizulia.util.Utils.mostrarAlerta
import com.nutrizulia.util.Utils.mostrarDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreated()
        viewModel.isInstitutionSelected.observe(this) { isInstitutionSelected ->
            if (!isInstitutionSelected) {
                mostrar()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.inicioFragment,
                R.id.consultasFragment,
                R.id.pacientesFragment,
                R.id.reportesFragment,
                R.id.cuentaFragment,
                R.id.ayudaFragment,
                R.id.seleccionarInstitucionFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupObservers()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cerrarSesion -> {
                    mostrarDialog(
                        context = this,
                        title = "Cerrar sesión",
                        message = "¿Estás seguro que quieres cerrar sesión?",
                        positiveButtonText = "Cerrar sesión",
                        onPositiveClick = {
                            viewModel.logout()
                        }
                    )
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.salir -> {
                    mostrarDialog(
                        context = this,
                        title = "Salir",
                        message = "¿Estás seguro que quieres salir de la aplicación?",
                        positiveButtonText = "Salir",
                        onPositiveClick = {
                            finish()
                        }
                    )
                    drawerLayout.closeDrawers()
                    true
                }

                else -> {
                    val handled = androidx.navigation.ui.NavigationUI.onNavDestinationSelected(
                        menuItem,
                        navController
                    )
                    if (handled) drawerLayout.closeDrawers()
                    handled
                }
            }
        }
    }

    private fun mostrar() {
        mostrarAlerta(
            context = this,
            title = "Error de autenticación",
            message = "No hay datos del usuario o la institución. Por favor, inicie sesión nuevamente.",
            positiveButtonText = "Cerrar sesión",
            onAcknowledge = {
                viewModel.logout()
            },
            isCancelable = false
        )
    }

    /**
     * Configura los observadores de LiveData del ViewModel.
     */
    private fun setupObservers() {
        viewModel.logoutComplete.observe(this) { isComplete ->
            if (isComplete) {
                navigateToLogin()
            }
        }
    }

    /**
     * Navega a la pantalla de Login y limpia el stack de actividades.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}