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
import com.nutrizulia.presentation.viewmodel.CatalogSyncViewModel
import com.nutrizulia.presentation.viewmodel.DataSyncViewModel
import com.nutrizulia.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SyncBatchFragment : Fragment() {

    private var _binding: FragmentSyncBatchBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Fragment binding is null")

    private val viewModel: SyncBatchViewModel by viewModels()
    private val catalogViewModel: CatalogSyncViewModel by viewModels()
    private val dataViewModel: DataSyncViewModel by viewModels()

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
            // Activar estado de carga inmediatamente para cubrir verificación de autenticación
            binding.cardSyncButton.isEnabled = false
            binding.tvSyncTitle.text = "Verificando sesión..."
            binding.tvSyncSubtitle.text = "Validando autenticación y permisos"
            binding.progressBarSync.visibility = View.VISIBLE

            viewModel.iniciarSincronizacion()
        }
    }

    private fun observePendingRecords() {
        lifecycleScope.launch {
            viewModel.totalPendingRecords.collect { totalRecords ->
                updatePendingRecordsUI(totalRecords)
            }
        }
    }

    private fun updatePendingRecordsUI(totalRecords: Int) {
        binding.tvTotalCount.text = totalRecords.toString()
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

        // Tras finalizar la sincronización de datos, proceder con sincronización de catálogos
        viewModel.onProceedCatalogSync = {
            startCatalogSync()
        }

        // Mostrar diálogo de sesión expirada/invalidada
        viewModel.onShowAuthExpiredDialog = dialogCallback@ { title, message ->
            if (_binding == null || !isAdded) return@dialogCallback
            // Restaurar UI al finalizar la verificación con sesión inválida
            restoreUI()
            binding.tvSyncSubtitle.text = "Sesión expirada o inválida"
            Utils.mostrarDialog(
                requireContext(),
                title,
                message,
                positiveButtonText = "Ir al inicio de sesión",
                negativeButtonText = "Cancelar",
                onPositiveClick = {
                    val intent = android.content.Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                },
                onNegativeClick = {
                    Log.i("SyncBatchFragment", "Sesión expirada: usuario canceló")
                },
                isCancelable = true
            )
        }
    }

    /**
     * Maneja el inicio de la sincronización
     */
    private fun onSyncStart(message: String) {
        // Verificar si el Fragment está en un estado válido
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Fragment not in valid state, skipping UI update")
            return
        }
        
        // Deshabilitar botón del encabezado y mostrar progreso
        binding.cardSyncButton.isEnabled = false
        binding.tvSyncTitle.text = "Sincronizando..."
        binding.tvSyncSubtitle.text = message
        binding.progressBarSync.visibility = View.VISIBLE
        // Evitar snackbar duplicado con el mismo mensaje del subtítulo
        // (Removemos el uso de showInfoMessage aquí para no duplicar)
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
        // Verificar si el Fragment está en un estado válido
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Fragment not in valid state, skipping UI update")
            return
        }
        
        // NO restaurar UI aquí para continuar con la actualización de catálogos
        // Mensaje de éxito
        val fullMessage = if (totalProcessed > 0) {
            "$message\n$successCount registros sincronizados correctamente"
        } else {
            "Sincronización de datos del usuario completada"
        }
        binding.tvSyncSubtitle.text = fullMessage

        // Mostrar reporte detallado en log o dialog si es necesario
        showDetailedReport("Sincronización Exitosa", detailedReport)
        // El ViewModel disparará startCatalogSync() mediante onProceedCatalogSync
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
        // Verificar si el Fragment está en un estado válido
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Fragment not in valid state, skipping UI update")
            return
        }
        
        // NO restaurar UI aquí para continuar con la actualización de catálogos
        val fullMessage = "$message\n" +
                "✅ $successCount exitosos, ❌ $failureCount fallidos de $totalProcessed total\n" +
                "Los registros fallidos se reintentarán en la próxima sincronización"
        binding.tvSyncSubtitle.text = fullMessage

        // Mostrar reporte detallado
        showDetailedReport("Sincronización Parcial", detailedReport)
        // Para evitar duplicar mensajes, mostramos un snackbar conciso distinto al subtítulo
        showWarningMessage("Algunos registros fallaron; se reintentarán automáticamente.")
        // El ViewModel disparará startCatalogSync() mediante onProceedCatalogSync
    }

    /**
     * Maneja los errores de sincronización
     */
    private fun onSyncError(message: String, details: String?) {
        // Verificar si el Fragment está en un estado válido
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Fragment not in valid state, skipping UI update")
            return
        }

        // Restaurar UI
        restoreUI()

        // Mensaje de error y subtítulo
        binding.tvSyncSubtitle.text = message
        val fullMessage = if (details != null) {
            "$message\n$details"
        } else {
            message
        }

        showErrorMessage(fullMessage)
    }

    /**
     * Lanza la sincronización de catálogos manteniendo el estado de carga
     */
    private fun startCatalogSync() {
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Fragment not in valid state, skipping catalog sync")
            return
        }

        binding.tvSyncTitle.text = "Actualizando catálogos..."
        binding.tvSyncSubtitle.text = "Sincronizando catálogos de la aplicación"
        binding.progressBarSync.visibility = View.VISIBLE
        binding.cardSyncButton.isEnabled = false

        catalogViewModel.syncCatalogsAsync { success ->
            if (!isAdded || _binding == null) return@syncCatalogsAsync

            if (success) {
                binding.tvSyncSubtitle.text = "Catálogos actualizados correctamente"
                showSuccessMessage("Catálogos actualizados correctamente")
            } else {
                val errorMsg = catalogViewModel.getCurrentErrorMessage() ?: "Error al actualizar catálogos"
                binding.tvSyncSubtitle.text = errorMsg
                showErrorMessage(errorMsg)
            }
            // Al finalizar la actualización de catálogos, encadenar sincronización de datos del usuario
            startDataSync()
        }
    }

    /**
     * Lanza la sincronización de datos del usuario manteniendo el estado de carga
     */
    private fun startDataSync() {
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Fragment not in valid state, skipping data sync")
            return
        }

        binding.tvSyncTitle.text = "Actualizando datos del usuario..."
        binding.tvSyncSubtitle.text = "Sincronizando datos del usuario"
        binding.progressBarSync.visibility = View.VISIBLE
        binding.cardSyncButton.isEnabled = false

        dataViewModel.syncUserDataAsync { success ->
            if (!isAdded || _binding == null) return@syncUserDataAsync

            if (success) {
                binding.tvSyncSubtitle.text = "Datos del usuario actualizados correctamente"
                showSuccessMessage("Datos del usuario actualizados correctamente")
            } else {
                val errorMsg = dataViewModel.getCurrentErrorMessage() ?: "Error al actualizar datos del usuario"
                binding.tvSyncSubtitle.text = errorMsg
                showErrorMessage(errorMsg)
            }
            // Al finalizar la sincronización de datos del usuario, restaurar la UI
            restoreUI()
        }
    }

    /**
     * Restaura la UI a su estado inicial
     */
    private fun restoreUI() {
        // Verificar si el binding está disponible
        if (_binding == null) {
            Log.w("SyncBatchFragment", "Binding is null, cannot restore UI")
            return
        }
        
        binding.cardSyncButton.isEnabled = true
        binding.tvSyncTitle.text = "Iniciar sincronización"
        binding.progressBarSync.visibility = View.GONE
    }

    private fun isDuplicateWithUi(message: String): Boolean {
        if (_binding == null) return false
        val title = binding.tvSyncTitle.text?.toString()?.trim() ?: ""
        val subtitle = binding.tvSyncSubtitle.text?.toString()?.trim() ?: ""
        val m = message.trim()
        return m.equals(title, ignoreCase = true) || m.equals(subtitle, ignoreCase = true)
    }

    /**
     * Muestra mensaje de información (azul)
     */
    private fun showInfoMessage(message: String) {
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Cannot show info message, Fragment not in valid state")
            return
        }
        if (isDuplicateWithUi(message)) return
        
        val snackbar = com.google.android.material.snackbar.Snackbar.make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.info_color)
        )
        snackbar.show()
    }

    /**
     * Muestra mensaje de éxito (verde)
     */
    private fun showSuccessMessage(message: String) {
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Cannot show success message, Fragment not in valid state")
            return
        }
        if (isDuplicateWithUi(message)) return
        
        val snackbar = com.google.android.material.snackbar.Snackbar.make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.success_color)
        )
        snackbar.show()
    }

    /**
     * Muestra mensaje de advertencia (amarillo/naranja)
     */
    private fun showWarningMessage(message: String) {
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Cannot show warning message, Fragment not in valid state")
            return
        }
        if (isDuplicateWithUi(message)) return
        
        val snackbar = com.google.android.material.snackbar.Snackbar.make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.warning_color)
        )
        snackbar.show()
    }

    /**
     * Muestra mensaje de error (rojo)
     */
    private fun showErrorMessage(message: String) {
        if (_binding == null || !isAdded) {
            Log.w("SyncBatchFragment", "Cannot show error message, Fragment not in valid state")
            return
        }
        if (isDuplicateWithUi(message)) return
        
        val snackbar = com.google.android.material.snackbar.Snackbar.make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.error_color)
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
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
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