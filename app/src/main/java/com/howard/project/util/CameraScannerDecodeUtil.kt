package com.howard.project.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

object CameraScannerDecodeUtil {

    fun getPicture(contentResolver: ContentResolver, selectedImage: Uri?): Bitmap? {
        if (selectedImage == null)
            return null

        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(selectedImage, filePathColumn, null, null, null)?.also {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(filePathColumn[0])
            val picturePath = it.getString(columnIndex)
            it.close()
            return BitmapFactory.decodeFile(picturePath)
        }

        return null
    }

    private fun bitmapResize(imageBitmap: Bitmap): Bitmap {
        var bitmap = imageBitmap
        val heightBmp = bitmap.height.toFloat()
        val widthBmp = bitmap.width.toFloat()

        val ratio: Float

        if (widthBmp > 640f) {
            ratio = widthBmp / 640f
        } else {
            //No need scale
            Log.d("BarCodeScannerDecodeUtil", "No need scale")
            return imageBitmap
        }

        // Get Screen width
        val height = heightBmp / ratio
        val width = widthBmp / ratio
        val convertHeight = height.toInt()
        val convertWidth = width.toInt()

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true)

        Log.d("BarCodeScannerDecodeUtil", "new bmp:${bitmap.width} ${bitmap.height}")
        return bitmap
    }

    fun scanCodeImage(bMap: Bitmap?): String? {
        if (bMap == null)
            return null

        //First check if bitmap too large and resize it
        val tmpBitMap = bitmapResize(bMap)

        var contents: String? = null
        val intArray = IntArray(tmpBitMap.width * tmpBitMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        tmpBitMap.getPixels(intArray, 0, tmpBitMap.width, 0, 0, tmpBitMap.width, tmpBitMap.height)
        val source = RGBLuminanceSource(tmpBitMap.width, tmpBitMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
        } catch (e: Exception) {
            Log.e("BarCodeScannerDecodeUtil", "Error decoding barcode", e)
        }
        return contents
    }
}