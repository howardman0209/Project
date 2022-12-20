package com.howard.project.ui.view


import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import com.howard.project.R
import com.howard.project.databinding.ActivityReadNfcBinding
import com.howard.project.extension.hexToByteArray
import com.howard.project.extension.toHexString
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.ReadNFCViewModel
import com.howard.project.util.APDU_COMMAND_READ_RECORD_1
import com.howard.project.util.APDU_COMMAND_READ_RECORD_2
import com.howard.project.util.APDU_COMMAND_READ_RECORD_3
import com.howard.project.util.APDU_RESPONSE_CODE_OK
import com.howard.project.util.ApduUtil.executeGPO
import com.howard.project.util.ApduUtil.findAID
import com.howard.project.util.ApduUtil.findPAN
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

        val hexChars = "0123456789ABCDEF"
        val hexToByteArray = hexChars.hexToByteArray()
        val toHexString = hexToByteArray.toHexString()
        Log.d(TAG, "hexChars :$hexChars")
        Log.d(TAG, "hexToByteArray :${hexToByteArray}")
        Log.d(TAG, "toHexString :${toHexString}")
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

//        val response1 = isoDep.transceive("00A404000E315041592E5359532E444446303100".hexToByteArray())
//        Log.d("onTagDiscovered", "response1: ${response1.toHexString()}")
//
//        val response2 = isoDep.transceive("00B2010C00".hexToByteArray())
//        Log.d("onTagDiscovered", "response2: ${response2.toHexString()}")
//
//        val response3 = isoDep.transceive("00B2020C00".hexToByteArray())
//        Log.d("onTagDiscovered", "response3: ${response3.toHexString()}")
//
//        val response4 = isoDep.transceive("00A4040007A000000003101000".hexToByteArray())
//        Log.d("onTagDiscovered", "response4: ${response4.toHexString()}")
//
//        val response7 = isoDep.transceive("80A80000238321F0204000000000001000000000000000057800000000000978220202001212121200".hexToByteArray())
//        Log.d("onTagDiscovered", "response7: ${response7.toHexString()}")
//
//        val response6 = isoDep.transceive("00B2010C00".hexToByteArray())
//        Log.d("onTagDiscovered", "response6: ${response6.toHexString()}")
//
//        val response5 = isoDep.transceive("00B2020C00".hexToByteArray())
//        Log.d("onTagDiscovered", "response5: ${response5.toHexString()}")

        isoDep.initCommand()
        val aid = isoDep.findAID()
        Log.d("onTagDiscovered", "AID: $aid")
        aid?.let {
            val selectAIDTlv = isoDep.selectAID(it)
            val pdol = isoDep.getRequiredPDOL(selectAIDTlv)
            val executeGPOTlv = isoDep.executeGPO(pdol, applicationContext)
            Log.d("onTagDiscovered", "executeGPOTlv: $executeGPOTlv")
            var pan = isoDep.findPAN(executeGPOTlv)
            if (pan == null) {
                val tlv1 = isoDep.transceive(APDU_COMMAND_READ_RECORD_1.hexToByteArray()).toHexString()
                val tlv2 = isoDep.transceive(APDU_COMMAND_READ_RECORD_2.hexToByteArray()).toHexString()
                val tlv3 = isoDep.transceive(APDU_COMMAND_READ_RECORD_3.hexToByteArray()).toHexString()
                pan = if(tlv1.endsWith(APDU_RESPONSE_CODE_OK)){
                    isoDep.findPAN(tlv1)
                }else if(tlv2.endsWith(APDU_RESPONSE_CODE_OK)){
                    isoDep.findPAN(tlv2)
                } else if (tlv3.endsWith(APDU_RESPONSE_CODE_OK)) {
                    isoDep.findPAN(tlv3)
                }else{
                    "4111111111111111"
                }
            }
            Log.d("onTagDiscovered", "pan: $pan")
        }
//        setResult(Activity.RESULT_OK, Intent().apply { putExtra(BUNDLE_READ_NFC_RESULT, msg) })
//        finish()
    }
}