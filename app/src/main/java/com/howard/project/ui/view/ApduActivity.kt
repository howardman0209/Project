package com.howard.project.ui.view

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView.State
import com.howard.project.R
import com.howard.project.databinding.ActivityApduBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewAdapter.ApduCommandAndResponseListAdapter
import com.howard.project.ui.viewModel.ApduViewModel
import com.howard.project.uiComponent.AppBarLayoutManager

class ApduActivity : MVVMActivity<ApduViewModel, ActivityApduBinding>(), NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null
    private var recyclerViewAdapter = ApduCommandAndResponseListAdapter()

    companion object {
        private val TAG = TestNfcActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.layoutManager = AppBarLayoutManager(this)
        binding.recyclerView.itemAnimator = null


        recyclerViewAdapter.setAdapterData(ArrayList(viewModel.apduCommandList))
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

    }

    fun sendCommand() {
        if (!binding.etApduCommand.text.isNullOrEmpty()) {
            viewModel.apduCommandList.add(binding.etApduCommand.text.toString())
            updateRecyclerView(viewModel.apduCommandList.toList(), recyclerViewAdapter)

            binding.etApduCommand.text?.clear()
        }
        Log.d(TAG, "apduCommandList: ${viewModel.apduCommandList}")
        binding.recyclerView.smoothScrollToPosition(viewModel.apduCommandList.size - 1)
    }

    private fun updateRecyclerView(dataList: List<String>, adapter: ApduCommandAndResponseListAdapter) {
        Log.d("@@", "DataList: $dataList")
        adapter.notifyItemInserted(dataList.size)
        adapter.setAdapterData(ArrayList(dataList))
    }
}