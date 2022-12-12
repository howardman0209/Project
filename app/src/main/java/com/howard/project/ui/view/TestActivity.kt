package com.howard.project.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.howard.project.R
import com.howard.project.databinding.ActivityTestBinding
import com.howard.project.extension.TAG
import com.howard.project.extension.requireFilePermission
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.model.FcmRedirectData
import com.howard.project.ui.viewModel.TestViewModel
import com.howard.project.util.BUNDLE_CAMERA_SCANNER_RESULT
import com.howard.project.util.BUNDLE_FCM_DATA

class TestActivity : MVVMActivity<TestViewModel, ActivityTestBinding>() {

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "pickImageLauncher result: ${result.data?.data}")
        }
    }

    private val cameraScannerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data == null) return@registerForActivityResult

            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "cameraScannerLauncher result: ${result.data?.getStringExtra(BUNDLE_CAMERA_SCANNER_RESULT)}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fcmData = intent?.getSerializableExtra(BUNDLE_FCM_DATA) as? FcmRedirectData
        Log.d("TestActivity", "fcmData: $fcmData")

        binding.testButton1.isEnabled = true
        binding.testButton2.isEnabled = true

        binding.testButton1.setOnClickListener {
            requireFilePermission(it) {
                pickImageLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
            }
        }

        binding.testButton2.setOnClickListener {
            cameraScannerLauncher.launch(Intent(this, ScanQrActivity::class.java))
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_test

    override fun getViewModelInstance(): TestViewModel = TestViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }

}