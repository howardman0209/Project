package com.howard.project.ui.viewModel

import android.content.ContentValues
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.howard.project.ui.base.BaseViewModel
import com.howard.project.util.NFCManager
import com.howard.project.util.NFCStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

class TestNfcViewModel : BaseViewModel() {

    companion object {
        private val TAG = TestNfcViewModel::class.java.simpleName
        private const val prefix = "android.nfc.tech."
    }

    private val liveNFC: MutableStateFlow<NFCStatus?>
    private val liveToast: MutableSharedFlow<String?>
    private val liveTag: MutableStateFlow<String?>

    init {
        Log.d(TAG, "constructor")
        liveNFC = MutableStateFlow(null)
        liveToast = MutableSharedFlow()
        liveTag = MutableStateFlow(null)
    }

    //region Toast Methods
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

    //endregion
    fun getNFCFlags(): Int {
        return NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE //or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
    }

    fun getExtras(): Bundle {
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 30000)
        return options
    }

    //region NFC Methods
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

    fun readTag(tag: Tag?, context: Context) {
        viewModelScope.launch {
            Log.d(TAG, "readTag(${tag} ${tag?.techList})")
            postNFCStatus(NFCStatus.Process, context)
            val stringBuilder: StringBuilder = StringBuilder()
            val id: ByteArray? = tag?.id
            stringBuilder.append("Tag ID (hex): ${getHex(id!!)} \n")
            stringBuilder.append("Tag ID (dec): ${getDec(id)} \n")
            stringBuilder.append("Tag ID (reversed): ${getReversed(id)} \n")
            stringBuilder.append("Technologies: ")
            tag.techList.forEach { tech ->
                stringBuilder.append(tech.substring(prefix.length))
                stringBuilder.append(", ")
            }
            stringBuilder.delete(stringBuilder.length - 2, stringBuilder.length)
            tag.techList.forEach { tech ->
                if (tech.equals(MifareClassic::class.java.name)) {
                    stringBuilder.append('\n')
                    val mifareTag: MifareClassic = MifareClassic.get(tag)
                    val type = when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> "Classic"
                        MifareClassic.TYPE_PLUS -> "Plus"
                        MifareClassic.TYPE_PRO -> "Pro"
                        else -> "Unknown"
                    }
                    stringBuilder.append("Mifare Classic type: $type \n")
                    stringBuilder.append("Mifare size: ${mifareTag.size} bytes \n")
                    stringBuilder.append("Mifare sectors: ${mifareTag.sectorCount} \n")
                    stringBuilder.append("Mifare blocks: ${mifareTag.blockCount}")
                }
                if (tech.equals(MifareUltralight::class.java.name)) {
                    stringBuilder.append('\n')
                    val mifareUlTag: MifareUltralight = MifareUltralight.get(tag)
                    val type = when (mifareUlTag.type) {
                        MifareUltralight.TYPE_ULTRALIGHT -> "Ultralight"
                        MifareUltralight.TYPE_ULTRALIGHT_C -> "Ultralight C"
                        else -> "Unknown"
                    }
                    stringBuilder.append("Mifare Ultralight type: ")
                    stringBuilder.append(type)
                }
            }
            Log.d(TAG, "Datum: $stringBuilder")
            Log.d(ContentValues.TAG, "dumpTagData Return \n$stringBuilder")
            postNFCStatus(NFCStatus.Read, context)
            liveTag.emit("${getDateTimeNow()} \n $stringBuilder")
        }
    }

    fun updateNFCStatus(status: NFCStatus, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            postNFCStatus(status, context)
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

    fun observeNFCStatus(): StateFlow<NFCStatus?> {
        return liveNFC.asStateFlow()
    }

    //endregion
    //region Tags Information Methods
    private fun getDateTimeNow(): String {
        Log.d(TAG, "getDateTimeNow()")
        val timeFormatter: DateFormat = SimpleDateFormat.getDateTimeInstance()
        val now = Date()
        Log.d(ContentValues.TAG, "getDateTimeNow() Return ${timeFormatter.format(now)}")
        return timeFormatter.format(now)
    }

    private fun getHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Int = bytes[i].and(0xff.toByte()).toInt()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0)
                sb.append(" ")
        }
        return sb.toString()
    }

    private fun getDec(bytes: ByteArray): Long {
        Log.d(TAG, "getDec()")
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value: Long = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun getReversed(bytes: ByteArray): Long {
        Log.d(TAG, "getReversed()")
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    fun observeLiveData(): StateFlow<String?> {
        return liveTag.asStateFlow()
    }
    //endregion
}