package com.nutrizulia.presentation.view.paciente

import android.app.Dialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nutrizulia.databinding.FragmentSeleccionarParentescoDialogBinding
import com.nutrizulia.domain.model.catalog.Parentesco
import com.nutrizulia.presentation.viewmodel.paciente.SeleccionarParentescoDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeleccionarParentescoDialogFragment : DialogFragment() {

    private val viewModel: SeleccionarParentescoDialogViewModel by viewModels()
    private var _binding: FragmentSeleccionarParentescoDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentSeleccionarParentescoDialogBinding.inflate(LayoutInflater.from(context))

        setupDropdownMenu()

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar parentesco")
            .setView(binding.root)
            .setPositiveButton("Aceptar") { _, _ ->
                val parentescoId = viewModel.selectedParentesco.value?.id
                if (parentescoId != null) {
                    setFragmentResult(
                        REQUEST_KEY,
                        bundleOf(BUNDLE_KEY_PARENTESCO to parentescoId)
                    )
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun setupDropdownMenu() {
        viewModel.obtenerParentescos()

        viewModel.parentescos.observe(this) {
            updateAdapter(binding.dropdownParentesco, it.map(Parentesco::nombre))
        }

        binding.dropdownParentesco.setOnItemClickListener { _, _, position, _ ->
            viewModel.parentescos.value?.get(position)?.let {
                viewModel.onParentescoSelected(it)
            }
        }
    }

    private fun updateAdapter(dropdown: AutoCompleteTextView, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        dropdown.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "SeleccionarParentescoDialog"
        const val REQUEST_KEY = "parentescoSelectionRequest"
        const val BUNDLE_KEY_PARENTESCO = "selectedParentesco"

        fun newInstance(): SeleccionarParentescoDialogFragment {
            return SeleccionarParentescoDialogFragment()
        }
    }
}
