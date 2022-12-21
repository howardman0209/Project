package com.howard.project.ui.view


import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import com.howard.project.R
import com.howard.project.data.CreditCardData
import com.howard.project.databinding.ActivityReadNfcBinding
import com.howard.project.extension.hexToByteArray
import com.howard.project.extension.toHexString
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.ReadNFCViewModel
import com.howard.project.util.*
import com.howard.project.util.ApduUtil.executeGPO
import com.howard.project.util.ApduUtil.findAID
import com.howard.project.util.ApduUtil.findCARD
import com.howard.project.util.ApduUtil.getRequiredPDOL
import com.howard.project.util.ApduUtil.initCommand
import com.howard.project.util.ApduUtil.selectAID


class ReadNFCActivity : MVVMActivity<ReadNFCViewModel, ActivityReadNfcBinding>(), NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null

    companion object {
        private val TAG = TestNfcActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun getLayoutResId(): Int = R.layout.activity_read_nfc

    override fun getViewModelInstance(): ReadNFCViewModel = ReadNFCViewModel()

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
        val msg = "NFC Detected: ${viewModel.getCurrentTime()}"
        viewModel.displayMessage.set(msg)
        //
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        Log.d("onTagDiscovered", "isoDep: $isoDep")

        isoDep.initCommand()
        val aid = isoDep.findAID()
        Log.d("onTagDiscovered", "AID: $aid")
        aid?.let {
            val selectAIDTlv = isoDep.selectAID(it)
            val pdol = isoDep.getRequiredPDOL(selectAIDTlv)
            val executeGPOTlv = isoDep.executeGPO(pdol, applicationContext)
            Log.d("onTagDiscovered", "executeGPOTlv: $executeGPOTlv")
            var card = isoDep.findCARD(executeGPOTlv)
            if (card == null) {
                val tlv1 = isoDep.transceive(APDU_COMMAND_READ_EF1_RECORD1.hexToByteArray()).toHexString()
                val tlv2 = isoDep.transceive(APDU_COMMAND_READ_EF1_RECORD2.hexToByteArray()).toHexString()
                val tlv3 = isoDep.transceive(APDU_COMMAND_READ_EF2_RECORD1.hexToByteArray()).toHexString()
                card = if(tlv1.endsWith(APDU_RESPONSE_CODE_OK)){
                    Log.d("onTagDiscovered", "tlv1: $tlv1")
                    isoDep.findCARD(tlv1)
                }else if(tlv2.endsWith(APDU_RESPONSE_CODE_OK)){
                    Log.d("onTagDiscovered", "tlv2: $tlv2")
                    isoDep.findCARD(tlv2)
                } else if (tlv3.endsWith(APDU_RESPONSE_CODE_OK)) {
                    Log.d("onTagDiscovered", "tlv3: $tlv3")
                    isoDep.findCARD(tlv3)
                }else{
                    CreditCardData(
                        pan = "4111111111111111",
                        expiry = "12/35",
                        cardHolderName = "TEST CARDHOLDER"
                    )
                }
            }
            Log.d("onTagDiscovered", "card: $card")
        }
//        setResult(Activity.RESULT_OK, Intent().apply { putExtra(BUNDLE_READ_NFC_RESULT, msg) })
//        finish()
    }
}