package com.howard.project.ui.viewModel

import android.content.Context
import android.nfc.NfcAdapter
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.howard.project.ui.base.BaseViewModel
import com.howard.project.util.NFCManager
import com.howard.project.util.NFCStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReadNFCViewModel : BaseViewModel() {
    private val liveNFC: MutableStateFlow<NFCStatus?>
    private val liveToast: MutableSharedFlow<String?>
    private val liveTag: MutableStateFlow<String?>

    val displayMessage = ObservableField<String>()

    companion object {
        private val TAG = TestNfcViewModel::class.java.simpleName
    }

    init {
        Log.d(TAG, "init: Mutable Live Data")
        liveNFC = MutableStateFlow(null)
        liveToast = MutableSharedFlow()
        liveTag = MutableStateFlow(null)
    }

    private fun updateToast(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            liveToast.emit(message)
        }
    }

    private suspend fun postToast(message: String) {
        liveToast.emit(message)
    }

    fun observeToast(): SharedFlow<String?> {
        return liveToast.asSharedFlow()
    }

    fun observeLiveData(): StateFlow<String?> {
        return liveTag.asStateFlow()
    }

    fun observeNFCStatus(): StateFlow<NFCStatus?> {
        return liveNFC.asStateFlow()
    }

    fun getNFCFlags(): Int {
        return NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE //or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
    }

    fun onCheckNFC(isChecked: Boolean, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onCheckNFC(${isChecked})")
            if (isChecked) {
                postNFCStatus(NFCStatus.Tap, context)
                liveTag.emit("Please Tap Now")
            } else {
                postNFCStatus(NFCStatus.NoOperation, context)
                liveTag.emit(null)
            }
        }
    }

    private suspend fun postNFCStatus(status: NFCStatus, context: Context) {
        Log.d(TAG, "postNFCStatus(${status})")
        if (NFCManager.isSupportedAndEnabled(context)) liveNFC.emit(status)
        else if (NFCManager.isNotEnabled(context)) {
            liveNFC.emit(NFCStatus.NotEnabled)
            postToast("Please Enable your NFC!")
        } else if (NFCManager.isNotSupported(context)) {
            liveNFC.emit(NFCStatus.NotSupported)
            postToast("NFC Not Supported!")
        }
    }
}