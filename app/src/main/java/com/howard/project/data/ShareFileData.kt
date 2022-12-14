package com.howard.project.data

import android.net.Uri
import java.io.File

data class ShareFileData(
    var file: File? = null,
    var uri: Uri? = null
)
