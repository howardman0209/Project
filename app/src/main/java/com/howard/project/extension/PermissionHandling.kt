package com.howard.project.extension

import android.Manifest
import android.os.Build
import android.view.View

import com.howard.project.ui.model.PermissionResult
import com.google.android.material.snackbar.Snackbar
import com.howard.project.ui.base.BaseActivity
import com.howard.project.util.REQUEST_CODE_FILE


inline fun BaseActivity.requireFilePermission(view: View, crossinline onSuccess: () -> Unit) {
    val requiredFilePermissions = listOfNotNull(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) Manifest.permission.WRITE_EXTERNAL_STORAGE else null
    ).toTypedArray()

    requirePermissions(requiredFilePermissions) {
        requestCode = REQUEST_CODE_FILE
        resultCallback = {
            when (this) {
                is PermissionResult.PermissionGranted -> {
                    onSuccess()
                }
                is PermissionResult.NeedRationale,
                is PermissionResult.PermissionDenied -> {
                    Snackbar
                        .make(view, "Allow ...", Snackbar.LENGTH_LONG)
                        .setAction("request") {
                            requestPermissions(requiredFilePermissions, REQUEST_CODE_FILE)
                        }
                        .show()
                }
                is PermissionResult.PermissionDeniedPermanently -> {
                    Snackbar
                        .make(view, "Allow ...", Snackbar.LENGTH_LONG)
                        .setAction("setting") {
                            (this@requireFilePermission).startAppSettings()
                        }
                        .show()
                }
            }
        }
    }
}