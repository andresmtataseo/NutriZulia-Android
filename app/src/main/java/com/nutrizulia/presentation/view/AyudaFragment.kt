package com.nutrizulia.presentation.view

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nutrizulia.databinding.FragmentAyudaBinding
import com.nutrizulia.presentation.viewmodel.AyudaViewModel
import androidx.core.net.toUri

class AyudaFragment : Fragment() {

    private val viewModel: AyudaViewModel by viewModels()
    private lateinit var binding: FragmentAyudaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAyudaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDescargar.setOnClickListener {
            viewModel.onDownloadManualClicked()
        }

        viewModel.downloadManualEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { url ->
                startManualDownload(url)
            }
        }
    }

    private fun startManualDownload(url: String) {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("application/pdf")
            .setTitle("Manual de usuario NutriZulia")
            .setDescription("Descargando manual-app.pdf")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setVisibleInDownloadsUi(true)

        val dm = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)

        Toast.makeText(requireContext(), "Descarga iniciada", Toast.LENGTH_SHORT).show()
    }
}