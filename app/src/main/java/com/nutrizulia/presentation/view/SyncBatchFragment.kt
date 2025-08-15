package com.nutrizulia.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nutrizulia.R
import com.nutrizulia.databinding.FragmentSyncBatchBinding
import com.nutrizulia.presentation.viewmodel.SyncBatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SyncBatchFragment : Fragment() {

    private var _binding: FragmentSyncBatchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SyncBatchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyncBatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModelCallbacks()
        observePendingRecords()
    }

    private fun setupUI() {
        // Configurar el botón de sincronización en el encabezado
        binding.cardSyncButton.setOnClickListener {
            viewModel.iniciarSincronizacion()
        }
    }

    private fun observePendingRecords() {
        lifecycleScope.launch {
            viewModel.pendingRecords.collect { pendingRecords ->
                updatePendingRecordsUI(pendingRecords)
            }
        }
    }

    private fun updatePendingRecordsUI(pendingRecords: com.nutrizulia.domain.usecase.dashboard.PendingRecordsByEntity) {
        binding.tvPacientesCount.text = pendingRecords.pacientes.toString()
        binding.tvConsultasCount.text = pendingRecords.consultas.toString()
        
        // Calcular total de registros
        val totalRegistros = pendingRecords.pacientes + pendingRecords.consultas + 
                           pendingRecords.signosVitales + pendingRecords.antropometricos + 
                           pendingRecords.diagnosticos + pendingRecords.otros
        binding.tvTotalCount.text = totalRegistros.toString()
        
        binding.tvAntropometricosCount.text = pendingRecords.antropometricos.toString()
        binding.tvDiagnosticosCount.text = pendingRecords.diagnosticos.toString()
        binding.tvOtrosCount.text = pendingRecords.otros.toString()
    }

    private fun setupViewModelCallbacks() {
        viewModel.onSyncStart = { message ->
            onSyncStart(message)
        }

        viewModel.onSyncSuccess = { successCount, totalProcessed, message, detailedReport ->
            onSyncSuccess(successCount, totalProcessed, message, detailedReport)
        }

        viewModel.onSyncPartialSuccess = { successCount, totalProcessed, failureCount, message, detailedReport ->
            onSyncPartialSuccess(successCount, totalProcessed, failureCount, message, detailedReport)
        }

        viewModel.onSyncError = { message, details ->
            onSyncError(message, details)
        }
    }

    /**
     * Maneja el inicio de la sincronización
     */
    private fun onSyncStart(message: String) {
        // Deshabilitar botón del encabezado y mostrar progreso
        binding.cardSyncButton.isEnabled = false
        binding.cardProgress.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        // Mostrar mensaje de inicio
        showInfoMessage(message)
    }

    /**
     * Maneja el éxito completo de la sincronización
     */
    private fun onSyncSuccess(
        successCount: Int,
        totalProcessed: Int,
        message: String,
        detailedReport: String
    ) {
        // Restaurar UI
        restoreUI()

        // Mostrar mensaje de éxito
        val fullMessage = if (totalProcessed > 0) {
            "$message\n$successCount registros sincronizados correctamente"
        } else {
            message
        }

        showSuccessMessage(fullMessage)

        // Mostrar reporte detallado en log o dialog si es necesario
        showDetailedReport("Sincronización Exitosa", detailedReport)
    }

    /**
     * Maneja el éxito parcial de la sincronización
     */
    private fun onSyncPartialSuccess(
        successCount: Int,
        totalProcessed: Int,
        failureCount: Int,
        message: String,
        detailedReport: String
    ) {
        // Restaurar UI
        restoreUI()

        // Mostrar mensaje de advertencia
        val fullMessage = "$message\n" +
                "✅ $successCount exitosos, ❌ $failureCount fallidos de $totalProcessed total\n" +
                "Los registros fallidos se reintentarán en la próxima sincronización"

        showWarningMessage(fullMessage)

        // Mostrar reporte detallado
        showDetailedReport("Sincronización Parcial", detailedReport)
    }

    /**
     * Maneja los errores de sincronización
     */
    private fun onSyncError(message: String, details: String?) {
        // Restaurar UI
        restoreUI()

        // Mostrar mensaje de error
        val fullMessage = if (details != null) {
            "$message\n$details"
        } else {
            message
        }

        showErrorMessage(fullMessage)
    }

    /**
     * Restaura la UI a su estado inicial
     */
    private fun restoreUI() {
        binding.cardSyncButton.isEnabled = true
        binding.cardProgress.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    /**
     * Muestra mensaje de información (azul)
     */
    private fun showInfoMessage(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.info_color)
        )
        snackbar.show()
    }

    /**
     * Muestra mensaje de éxito (verde)
     */
    private fun showSuccessMessage(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.success_color)
        )
        snackbar.show()
    }

    /**
     * Muestra mensaje de advertencia (amarillo/naranja)
     */
    private fun showWarningMessage(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.warning_color)
        )
        snackbar.show()
    }

    /**
     * Muestra mensaje de error (rojo)
     */
    private fun showErrorMessage(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.error_color)
        )
        snackbar.show()
    }

    /**
     * Muestra el reporte detallado en un dialog o log
     * Por ahora lo mostramos en el log, pero se puede cambiar a un dialog
     */
    private fun showDetailedReport(title: String, report: String) {
        Log.d("SyncBatchFragment", "$title:\n$report")

        // Opcional: Mostrar en un dialog si se desea
        // showReportDialog(title, report)
    }

    /**
     * Opcional: Método para mostrar el reporte en un dialog
     */
    private fun showReportDialog(title: String, report: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(report)
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}