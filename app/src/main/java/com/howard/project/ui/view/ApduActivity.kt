package com.howard.project.ui.view

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.ScrollView
import android.widget.TextView
import com.howard.project.R
import com.howard.project.databinding.ActivityApduBinding
import com.howard.project.extension.hexToByteArray
import com.howard.project.extension.toHexString
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.ApduViewModel


class ApduActivity : MVVMActivity<ApduViewModel, ActivityApduBinding>(), NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null

    companion object {
        private val TAG = TestNfcActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        viewModel.cardResponse.observe(this) {
            Log.d("observe", "cardResponseList: ${viewModel.cardResponse}")
            binding.cardResponseList.addView(
                TextView(this).apply {
                    text = it.uppercase()
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                    setBackgroundResource(outValue.resourceId)
                    isClickable = true
                    setOnClickListener {
                        copyToClipboard(text.toString())
                    }
                }
            )

            binding.bouncyNestedScrollView2.post {
                binding.bouncyNestedScrollView2.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }


    override fun getLayoutResId(): Int = R.layout.activity_apdu

    override fun getViewModelInstance(): ApduViewModel = ApduViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d("onTagDiscovered", "NFC Tag: $tag")
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        try {
            viewModel.apduCommandList.forEachIndexed { index, element ->
                val tlv = isoDep.transceive(element.hexToByteArray()).toHexString()
                if (index == viewModel.apduCommandList.lastIndex) {
                    Log.d("onTagDiscovered", "tlv: $tlv")
                    viewModel.cardResponse.postValue(tlv)
                }
            }
        } catch (e: Exception) {
            Log.d("onTagDiscovered", "Exception: $e")
        }
    }

    fun sendCommand() {
        if (!binding.etApduCommand.text.isNullOrEmpty()) {
            viewModel.apduCommandList.add(binding.etApduCommand.text.toString())
            binding.apduCommandList.addView(
                TextView(this).apply {
                    text = binding.etApduCommand.text.toString().uppercase()
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                    setBackgroundResource(outValue.resourceId)
                    isClickable = true
                    setOnClickListener {
                        copyToClipboard(text.toString())
                    }
                }
            )

            binding.etApduCommand.text?.clear()
        }
        Log.d(TAG, "apduCommandList: ${viewModel.apduCommandList}")
        binding.bouncyNestedScrollView.post {
            binding.bouncyNestedScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}