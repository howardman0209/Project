package com.howard.project.util

import android.util.Log
import com.howard.project.extension.TAG

object TlvUtil {

    fun findTagData(tag: String, tlv: String): String {
        val dataSize = tlv.substring(tlv.indexOf(tag.lowercase()) + tag.length, tlv.indexOf(tag.lowercase()) + tag.length + 2)
//        Log.d(TAG, "dataSize: $dataSize")
        val data = tlv.substring(tlv.indexOf(tag.lowercase()) + tag.length + 2, tlv.indexOf(tag.lowercase()) + tag.length + 2 + dataSize.toInt(16) * 2)
        Log.d(TAG, "$tag: $data")
        return data
    }

}