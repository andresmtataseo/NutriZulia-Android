package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nutrizulia.util.ApiConstants
import com.nutrizulia.util.PublicEndpoints
import com.nutrizulia.util.Event

class AyudaViewModel : ViewModel() {
    private val _downloadManualEvent = MutableLiveData<Event<String>>()
    val downloadManualEvent: LiveData<Event<String>> = _downloadManualEvent

    fun onDownloadManualClicked() {
        val base = ApiConstants.BASE_URL.trimEnd('/')
        val url = "$base${PublicEndpoints.MANUAL_APP}"
        _downloadManualEvent.value = Event(url)
    }
}