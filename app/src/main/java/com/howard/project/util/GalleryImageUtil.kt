package com.howard.project.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.howard.project.extension.TAG
import java.io.File

object GalleryImageUtil {

    fun getPictureFile(contentResolver: ContentResolver, selectedImage: Uri?): File? {
        if (selectedImage == null)
            return null

        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        try {
            contentResolver.query(selectedImage, filePathColumn, null, null, null)?.also {
                it.moveToFirst()
                val columnIndex = it.getColumnIndex(filePathColumn[0])
                val picturePath = it.getString(columnIndex)
                Log.d(TAG, "columnIndex: $columnIndex, picturePath: $picturePath")
                it.close()
                return File(picturePath)
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    fun getPictureBitmap(contentResolver: ContentResolver, selectedImage: Uri?): Bitmap? {
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
}