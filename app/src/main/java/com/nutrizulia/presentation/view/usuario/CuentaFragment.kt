package com.nutrizulia.presentation.view.usuario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentCuentaBinding
import com.nutrizulia.presentation.viewmodel.usuario.CuentaViewModel
import com.nutrizulia.util.Utils.mostrarSnackbar
import com.nutrizulia.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CuentaFragment : Fragment() {

    private val viewModel: CuentaViewModel by viewModels()
    private lateinit var binding: FragmentCuentaBinding;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.mensaje.observe(viewLifecycleOwner) { mensaje ->
            if (mensaje.isNotBlank()) mostrarSnackbar(binding.root, mensaje)
        }

        viewModel.salir.observe(viewLifecycleOwner) { salir ->
            if (salir) findNavController().popBackStack()
        }
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario == null) return@observe
            binding.tvCedula.text = usuario.cedula
            binding.tvNombres.text = usuario.nombres
            binding.tvFechaNacimiento.text = usuario.fechaNacimiento.toString()
            binding.ivGenero.setImageResource(if (usuario.genero == "MASCULINO") R.drawable.ic_male else R.drawable.ic_female)
            binding.tvGenero.text = usuario.genero
            // Editables
            binding.tvTelefono.text = usuario.telefono
            binding.tvCorreo.text = usuario.correo
            setupListeners(usuario.id)
        }
    }

    private fun setupListeners(id: Int) {
        binding.layoutTelefono.setOnClickListener {
            if (checkInternetConnection()) {
                findNavController().navigate(CuentaFragmentDirections.actionCuentaFragmentToEditarTelefonoFragment(id))
            }
        }

        binding.layoutCorreo.setOnClickListener {
            if (checkInternetConnection()) {
                findNavController().navigate(CuentaFragmentDirections.actionCuentaFragmentToEditarCorreoFragment(id))
            }
        }

        binding.layoutCambiarContrasena.setOnClickListener {
            if (checkInternetConnection()) {
                findNavController().navigate(CuentaFragmentDirections.actionCuentaFragmentToEditarClaveFragment(id))
            }
        }

        binding.layoutCambiarMaximoCitas.setOnClickListener {
            findNavController().navigate(R.id.action_cuentaFragment_to_editarDiasCitaFragment)
        }

    }

    /**
     * Verifica si hay conexión a internet antes de permitir acceso a configuraciones
     * que requieren conectividad de red
     * @return true si hay conexión, false si no la hay
     */
    private fun checkInternetConnection(): Boolean {
        return NetworkUtils.checkInternetConnectionWithCallback(requireContext()) {
            mostrarSnackbar(
                binding.root,
                "No hay conexión a internet. Verifica tu conexión e intenta nuevamente."
            )
        }
    }

}