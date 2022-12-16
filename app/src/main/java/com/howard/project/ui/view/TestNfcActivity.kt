package com.howard.project.ui.view

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.howard.project.R
import com.howard.project.databinding.ActivityTestNfcBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.TestNfcViewModel
import com.howard.project.util.NFCManager
import com.howard.project.util.NFCStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TestNfcActivity : MVVMActivity<TestNfcViewModel, ActivityTestNfcBinding>(), CompoundButton.OnCheckedChangeListener, NfcAdapter.ReaderCallback {

    companion object {
        private val TAG = TestNfcActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toggleButton.setOnCheckedChangeListener(this)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch{
                    viewModel.observeNFCStatus().collectLatest(action = { status ->
                        when(status){
                            NFCStatus.NoOperation->{
                                NFCManager.disableReaderMode(
                                    applicationContext,
                                    this@TestNfcActivity
                                )
                            }
                            NFCStatus.Tap ->{
                                NFCManager.enableReaderMode(
                                    applicationContext,
                                    this@TestNfcActivity,
                                    this@TestNfcActivity,
                                    viewModel.getNFCFlags(),
                                    viewModel.getExtras()
                                )
                            }
                            else->{}
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
                        binding.textViewExplanation.text = tag
                    })
                })
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == binding.toggleButton)
            viewModel.onCheckNFC(isChecked, applicationContext)
    }

    override fun onTagDiscovered(tag: Tag?) {
        viewModel.readTag(tag, applicationContext)
    }

    override fun getLayoutResId(): Int = R.layout.activity_test_nfc

    override fun getViewModelInstance(): TestNfcViewModel = TestNfcViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

}
