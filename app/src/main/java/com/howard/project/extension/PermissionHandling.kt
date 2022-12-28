package com.howard.project.extension

import android.Manifest.permission
import android.os.Build
import android.view.View
import com.howard.project.ui.model.PermissionResult.*
import com.google.android.material.snackbar.Snackbar
import com.howard.project.R
import com.howard.project.ui.base.BaseActivity
import com.howard.project.util.*


inline fun BaseActivity.requireFilePermission(view: View, crossinline onSuccess: () -> Unit) {
    val requiredFilePermissions = listOfNotNull(
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) permission.READ_EXTERNAL_STORAGE else permission.READ_MEDIA_IMAGES,
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) permission.WRITE_EXTERNAL_STORAGE else null
    ).toTypedArray()

    requirePermissions(requiredFilePermissions) {
        requestCode = REQUEST_CODE_FILE
        resultCallback = {
            when (this) {
                is PermissionGranted -> {
                    onSuccess()
                }
                is NeedRationale,
                is PermissionDenied -> {
                    Snackbar
                        .make(view, R.string.label_rationale_file_read, Snackbar.LENGTH_LONG)
                        .setAction(R.string.button_request) {
                            requestPermissions(requiredFilePermissions, REQUEST_CODE_FILE)
                        }
                        .show()
                }
                is PermissionDeniedPermanently -> {
                    Snackbar
                        .make(view, R.string.label_rationale_file_read, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_setting) {
                            (this@requireFilePermission).startAppSettings()
                        }
                        .show()
                }
            }
        }
    }
}

inline fun BaseActivity.requireLocationPermission(view: View, crossinline onSuccess: () -> Unit) {
    val requiredLocationPermissions = listOfNotNull(
        permission.ACCESS_COARSE_LOCATION,
        permission.ACCESS_FINE_LOCATION
    ).toTypedArray()

    requirePermissions(requiredLocationPermissions) {
        requestCode = REQUEST_CODE_LOCATION
        resultCallback = {
            when (this) {
                is PermissionGranted -> {
                    onSuccess()
                }
                is NeedRationale,
                is PermissionDenied -> {
                    Snackbar
                        .make(view, R.string.label_rationale_location, Snackbar.LENGTH_LONG)
                        .setAction(R.string.button_request) {
                            requestPermissions(requiredLocationPermissions, REQUEST_CODE_LOCATION)
                        }
                        .show()
                }
                is PermissionDeniedPermanently -> {
                    Snackbar
                        .make(view, R.string.label_rationale_location, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_setting) {
                            (this@requireLocationPermission).startAppSettings()
                        }
                        .show()
                }
            }
        }
    }
}

inline fun BaseActivity.requireCameraPermission(view: View, anchorView: View? = null, crossinline onSuccess: () -> Unit) {
    val camPermission = arrayOf(permission.CAMERA)
    requirePermissions(camPermission) {
        requestCode = REQUEST_CODE_CAMERA
        resultCallback = {
            when (this) {
                is PermissionGranted -> onSuccess()
                is NeedRationale,
                is PermissionDenied -> {
                    val snackBar = Snackbar
                        .make(view, R.string.label_rationale_camera, Snackbar.LENGTH_LONG)
                        .setAction(R.string.button_request) {
                            requestPermissions(camPermission, REQUEST_CODE_CAMERA)
                        }
                    snackBar.anchorView = anchorView
                    snackBar.show()
                }
                is PermissionDeniedPermanently -> {
                    val snackBar = Snackbar
                        .make(view, R.string.label_rationale_camera, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_setting) {
                            startAppSettings()
                        }
                    snackBar.anchorView = anchorView
                    snackBar.show()
                }
            }
        }
    }
}

inline fun BaseActivity.requireNotificationPermission(crossinline onSuccess: () -> Unit) {
    val requireNotificationPermission = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permission.POST_NOTIFICATIONS else null
    ).toTypedArray()

    requirePermissions(requireNotificationPermission) {
        requestCode = REQUEST_CODE_NOTIFICATION
        // Not mandated to allow notification permission
        resultCallback = { onSuccess() }
    }
}

inline fun BaseActivity.requireBluetoothPermission(view: View, crossinline onSuccess: () -> Unit) {
    val btDiscoverPermission = listOfNotNull(
        permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) permission.BLUETOOTH_CONNECT else null
    ).toTypedArray()

    requirePermissions(btDiscoverPermission) {
        requestCode = REQUEST_CODE_BLUETOOTH
        resultCallback = {
            when (this) {
                is PermissionGranted -> onSuccess()
                is NeedRationale,
                is PermissionDenied -> {
                    //do on rationale needed
                    Snackbar
                        .make(view, R.string.label_rationale_bluetooth, Snackbar.LENGTH_LONG)
                        .setAction(R.string.button_request) {
                            requestPermissions(btDiscoverPermission, REQUEST_CODE_BLUETOOTH)
                        }
                        .show()
                }
                is PermissionDeniedPermanently -> {
                    //do on perm denied
                    //show prompt app setting ui
                    Snackbar
                        .make(view, R.string.label_rationale_bluetooth, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_setting) { startAppSettings() }
                        .show()
                }
            }
        }
    }
}

inline fun BaseActivity.requireBluetoothPermissionForPrinter(view: View, crossinline onSuccess: () -> Unit) {
    val btDiscoverPermission = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) permission.BLUETOOTH_SCAN else null
    ).toTypedArray()

    if (btDiscoverPermission.isNotEmpty()) {
        requirePermissions(btDiscoverPermission) {
            requestCode = REQUEST_CODE_BLUETOOTH
            resultCallback = {
                when (this) {
                    is PermissionGranted -> onSuccess()
                    is NeedRationale,
                    is PermissionDenied -> {
                        //do on rationale needed
                        Snackbar
                            .make(view, R.string.label_rationale_bluetooth, Snackbar.LENGTH_LONG)
                            .setAction(R.string.button_request) {
                                requestPermissions(btDiscoverPermission, REQUEST_CODE_BLUETOOTH)
                            }
                            .show()
                    }
                    is PermissionDeniedPermanently -> {
                        //do on perm denied
                        //show prompt app setting ui
                        Snackbar
                            .make(view, R.string.label_rationale_bluetooth, Snackbar.LENGTH_LONG)
                            .setAction(R.string.label_setting) { startAppSettings() }
                            .show()
                    }
                }
            }
        }
    } else {
        onSuccess()
    }
}

inline fun BaseActivity.requireReadPhoneStatePermission(view: View, crossinline onSuccess: () -> Unit) {
    val requireReadPhoneStatePermission = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) permission.READ_PHONE_STATE else null
    ).toTypedArray()

    requirePermissions(requireReadPhoneStatePermission) {
        requestCode = REQUEST_CODE_PHONE_STATE
        // Not mandated to allow notification permission
        resultCallback = {
            when (this) {
                is PermissionGranted -> onSuccess()
                is NeedRationale,
                is PermissionDenied -> {
                    //do on rationale needed
                    Snackbar
                        .make(view, R.string.label_rationale_phone_state, Snackbar.LENGTH_LONG)
                        .setAction(R.string.button_request) {
                            requestPermissions(requireReadPhoneStatePermission, REQUEST_CODE_PHONE_STATE)
                        }
                        .show()
                }
                is PermissionDeniedPermanently -> {
                    //do on perm denied
                    //show prompt app setting ui
                    Snackbar
                        .make(view, R.string.label_rationale_phone_state, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_setting) { startAppSettings() }
                        .show()
                }
            }
        }
    }
}