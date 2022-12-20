package com.howard.project.util

import android.content.Context
import android.nfc.tech.IsoDep
import android.util.Log
import com.howard.project.extension.TAG
import com.howard.project.extension.hexToByteArray
import com.howard.project.extension.toHexString
import okhttp3.internal.toHexString
import org.json.JSONObject

object ApduUtil {
    fun IsoDep.initCommand(): String {
        return this.transceive(APDU_COMMAND_INIT.hexToByteArray()).toHexString()
    }

    fun IsoDep.findAID(): String? {
        var aid: String? = null

        var tlv = this.transceive(APDU_COMMAND_READ_RECORD_1.hexToByteArray()).toHexString()
        Log.d(TAG, "tlv: $tlv")
        if (!tlv.contains("4f")) {
            tlv = this.transceive(APDU_COMMAND_READ_RECORD_2.hexToByteArray()).toHexString()
            Log.d(TAG, "tlv: $tlv")
            if (!tlv.contains("4f")) {
                Log.d(TAG, "aid not found: return null AID")
                return null
            }
        }

        aid = TlvUtil.findTagData("4f", tlv)
        return aid
    }

    fun IsoDep.selectAID(aid: String): String {
        val cla = "00"
        val ins = "A4"
        val p1 = "04"
        val p2 = "00"
        var aidSize = (aid.length / 2).toHexString().uppercase()
        if (aidSize.length < 2) {
            aidSize = "0$aidSize"
        }
        Log.d(TAG, "aidSize: $aidSize")
        val le = "00"
        val apduCommand = "$cla$ins$p1$p2$aidSize${aid.uppercase()}$le"
        Log.d(TAG, "apduCommand: $apduCommand")
        val tlv = this.transceive(apduCommand.hexToByteArray()).toHexString()
        Log.d(TAG, "tlv: $tlv")
        return tlv
//        var tlv = this.transceive(APDU_COMMAND_READ_RECORD_1.hexToByteArray()).toHexString()
//        Log.d(TAG, "tlv: $tlv")
//        if (!tlv.contains("57")) {
//            tlv = this.transceive(APDU_COMMAND_READ_RECORD_2.hexToByteArray()).toHexString()
//            Log.d(TAG, "tlv: $tlv")
//            if (!tlv.contains("57")) {
//                Log.d(TAG, "aid null: return test card PAN")
//                return "4111111111111111"
//            }
//        }
//        Log.d(TAG, "tlv: $tlv")
    }

    fun IsoDep.getRequiredPDOL(tlv: String): String? {
        if (!tlv.contains("9f38")) {
            return null
        }

        val pdol = TlvUtil.findTagData("9f38", tlv)
        Log.d(TAG, "pdol: $pdol")
        return pdol
    }

    fun IsoDep.executeGPO(pdol: String?, context: Context): String {
        if (pdol == null) {
            return this.transceive(APDU_COMMAND_GPO_WITHOUT_PDOL.hexToByteArray()).toHexString()
        } else {
            val cla = "80"
            val ins = "A8"
            val p1 = "00"
            val p2 = "00"

            val emvTags = context.assets.open("emv/emvTags").bufferedReader().use { JSONObject(it.readText()) }
            val pdolTemplate = context.assets.open("emv/pdolTemplate").bufferedReader().use { JSONObject(it.readText()) }
            Log.d(TAG, "emvTags: $emvTags")

            val pdolTagAndLength: MutableMap<String, Int> = mutableMapOf()
            var cursor = 0
            while (cursor < pdol.length) {
                val tag = if (emvTags.has(pdol.substring(cursor, cursor + 2).uppercase())) {
                    pdol.substring(cursor, cursor + 2)
                } else {
                    pdol.substring(cursor, cursor + 4)
                }
//                Log.d(TAG, "emvTag: $tag, cursor: $cursor")
                val dataLength = pdol.substring(cursor + tag.length, cursor + tag.length + 2).toInt(16)
                pdolTagAndLength[tag] = dataLength
                cursor += (tag.length + 2)
            }
            Log.d(TAG, "pdolTagAndLength: $pdolTagAndLength")

            var dataSize = 0
            var data = ""
            pdolTagAndLength.forEach {
                dataSize += it.value
                data += if (pdolTemplate.has(it.key.uppercase())) {
                    if (!pdolTemplate.getString(it.key.uppercase()).isNullOrEmpty()) {
                        pdolTemplate.getString(it.key.uppercase())
                    } else {
                        "00".repeat(it.value)
                    }
                } else {
                    "00".repeat(it.value)
                }
            }
//            Log.d(TAG, "dataSize: $dataSize")

            var lc = (dataSize + 2).toHexString()
            if (lc.length < 2) {
                lc = "0$lc"
            }
            Log.d(TAG, "lc: $lc")
            val fixByte = "83"
            var dataSizeHex = dataSize.toHexString()
            if (dataSizeHex.length < 2) {
                dataSizeHex = "0$dataSizeHex"
            }
            Log.d(TAG, "dataSizeHex: $dataSizeHex")

//            val data = "00".repeat(dataSize)
            val le = "00"

            val apduCommand = "$cla$ins$p1$p2$lc$fixByte$dataSizeHex$data$le"
            Log.d(TAG, "apduCommand: $apduCommand")
            return this.transceive(apduCommand.hexToByteArray()).toHexString()
        }
    }

    fun IsoDep.findPAN(tlv: String): String? {
        if (!tlv.endsWith(APDU_RESPONSE_CODE_OK)) {
            Log.d(TAG, "Fail to executeGPO, return test card PAN")
            return null
        } else {
            var pan: String? = null
            // search for 5A tag - plain PAN
            if (tlv.contains("5A")) {
                pan = TlvUtil.findTagData("5A", tlv)
            } else if (tlv.contains("57")) {
                val tag57Data = TlvUtil.findTagData("57", tlv)
                pan = tag57Data.substring(0, tag57Data.indexOf('d'))
            }

//            Log.d(TAG, "5A and 57 Tag are missing, return test card PAN")
            return pan
        }
    }
}
