package com.howard.project.ui.view


import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.howard.project.R
import com.howard.project.databinding.ActivityReadNfcBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.ReadNFCViewModel
import com.howard.project.util.BUNDLE_READ_NFC_RESULT
import com.howard.project.util.NFCManager
import com.howard.project.util.NFCStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ReadNFCActivity : MVVMActivity<ReadNFCViewModel, ActivityReadNfcBinding>(), NfcAdapter.ReaderCallback {
    companion object {
        private val TAG = TestNfcActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCheckNFC(true, applicationContext)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.observeNFCStatus().collectLatest(action = { status ->
                        when (status) {
                            NFCStatus.NoOperation -> {
                                NFCManager.disableReaderMode(
                                    applicationContext,
                                    this@ReadNFCActivity
                                )
                            }
                            NFCStatus.Tap -> {
                                NFCManager.enableReaderMode(
                                    applicationContext,
                                    this@ReadNFCActivity,
                                    this@ReadNFCActivity,
                                    viewModel.getNFCFlags(),
                                    null
                                )
                            }
                            else -> {}
                        }
                    })
                }
                launch(block = {
                    viewModel.observeToast().collectLatest(action = { message ->
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    })
                })
                launch(block = {
                    viewModel.observeLiveData().collectLatest(action = { tag ->
                        Log.d("observeLiveData", "NFC message: $tag")
                        viewModel.displayMessage.set(tag)
                    })
                })
            }
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_read_nfc

    override fun getViewModelInstance(): ReadNFCViewModel = ReadNFCViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d("onTagDiscovered", "NFC Tag: $tag")
        val msg = "NFC Detected: ${viewModel.getCurrentTime()}"
        viewModel.displayMessage.set(msg)
        setResult(Activity.RESULT_OK, Intent().apply { putExtra(BUNDLE_READ_NFC_RESULT, msg) })
        finish()
    }
}