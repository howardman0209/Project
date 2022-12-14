package com.howard.project.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.howard.project.R
import com.howard.project.data.ShareFileData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ShareFileUtil {

    fun saveImage(context: Context, bitmap: Bitmap, filePrefix: String): ShareFileData? {
        val folderName = context.getString(R.string.app_name)
        val fileName = "$filePrefix${System.currentTimeMillis()}"
        val shareFileData = ShareFileData()

        //check api version >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.TITLE, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$folderName")
                // Add the date meta data to ensure the image is added at the front of the gallery
                put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis().toString())
                put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis().toString())
            }

            val resolver = context.contentResolver
            var uri: Uri? = null
            try {
                uri = resolver.insert(Media.EXTERNAL_CONTENT_URI, values)
                shareFileData.uri = uri
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        return if (!bitmap.compress(PNG, 100, outputStream)) {
                            Log.d("saveImage", "Failed to save bitmap.")
                            null
                        } else {
                            shareFileData.file = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/$folderName", "$fileName.png")
                            shareFileData
                        }
                    } ?: run {
                        Log.d("saveImage", "Failed to create new MediaStore record.")
                        null
                    }
                } ?: run {
                    Log.d("saveImage", "Failed to create new MediaStore record.")
                    return null
                }
            } catch (e: IOException) {
                uri?.let { orphanUri ->
                    // Don't leave an orphan entry in the MediaStore
                    resolver.delete(orphanUri, null, null)
                }
                return null
            }
        } else {
            val imagePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/$folderName"
            val imageDir = File(imagePath)
            if (!imageDir.exists()) imageDir.mkdirs()
            val imageName = "$fileName.png"
            val imageFile = File(imagePath, imageName)

            try {
                FileOutputStream(imageFile).use {
                    return if (!bitmap.compress(PNG, 100, it)) {
                        Log.d("saveImage", "Failed to save bitmap.")
                        null
                    } else {
                        shareFileData.file = File(imagePath, "$fileName.png")
                        shareFileData
                    }
                }
            } catch (e: IOException) {
                Log.d("saveImage", e.toString())
                return null
            }
        }

    }

    fun sendFileOut(context: Context, shareFileData: ShareFileData, dataType: String) {
        Log.d("sendFileOut", "${shareFileData.file?.absolutePath}")
        shareFileData.file?.let { file ->
            Intent(Intent.ACTION_SEND).also {
                var fileUri: Uri? = null
                shareFileData.uri?.let { uri ->
                    fileUri = uri
                } ?: run {
                    fileUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                }
                it.putExtra(Intent.EXTRA_STREAM, fileUri)
                it.setDataAndType(fileUri, dataType)
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                it.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(context, it, null)
            }
        }
    }

    fun sendLinkOut(context: Context, msg: String) {
        Intent(Intent.ACTION_SEND).also {
            it.putExtra(Intent.EXTRA_TEXT, msg)
            it.type = "text/plain"
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, it, null)
        }
    }

}