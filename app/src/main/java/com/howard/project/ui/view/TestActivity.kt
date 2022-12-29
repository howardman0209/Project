package com.howard.project.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.howard.project.R
import com.howard.project.data.FcmRedirectData
import com.howard.project.databinding.ActivityTestBinding
import com.howard.project.extension.TAG
import com.howard.project.extension.requireFilePermission
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.TestViewModel
import com.howard.project.util.*
import com.squareup.picasso.Picasso
import java.io.File


class TestActivity : MVVMActivity<TestViewModel, ActivityTestBinding>() {

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "pickImageLauncher result: ${result.data?.data}")
            val imageFile = GalleryImageUtil.getPictureFile(contentResolver, result.data?.data)

            if (imageFile == null) {
                Snackbar.make(binding.root, "Upload image fail", Snackbar.LENGTH_LONG).show()
            } else {
                setImage(imageFile, binding.uploadedPreview)
            }
        }
    }

    private val cameraScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "cameraScannerLauncher result: ${result.data?.getStringExtra(BUNDLE_CAMERA_SCANNER_RESULT)}")
        }
    }

    private val readNfcActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult

        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "readNfcActivityLauncher result: ${result.data?.getStringExtra(BUNDLE_READ_NFC_RESULT)}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fcmData = intent?.getSerializableExtra(BUNDLE_FCM_DATA) as? FcmRedirectData
        Log.d("TestActivity", "fcmData: $fcmData")

        binding.testButton1.isEnabled = true
        binding.testButton2.isEnabled = true
        binding.testButton3.isEnabled = true
        binding.testButton4.isEnabled = true
        binding.testButton5.isEnabled = true

        binding.testButton1.setOnClickListener {
            requireFilePermission(it) {
                pickImageLauncher.launch(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                )
            }
        }

        binding.testButton2.setOnClickListener {
            cameraScannerLauncher.launch(Intent(applicationContext, ScanQrActivity::class.java))
        }

        binding.testButton3.setOnClickListener {
            popUpDialogSelect()
        }

        binding.testButton3.setOnLongClickListener {
            popUpDialogConfirm()
            true
        }

        binding.testButton4.setOnClickListener {
//            startActivity(Intent(applicationContext, TestNfcActivity::class.java))
            readNfcActivityLauncher.launch(Intent(applicationContext, ReadNFCActivity::class.java))
//            startActivity(Intent(applicationContext, ReadNFCActivity::class.java))
        }

        binding.testButton4.setOnLongClickListener {
            startActivity(Intent(applicationContext, ApduActivity::class.java))
            true
        }

        binding.testButton5.setOnClickListener {
            NotificationUtil.testNotification(applicationContext, "hello world")
        }

        binding.testButton5.setOnLongClickListener {
            viewModel.testingApi()
            true
        }

    }

    private fun popUpDialogSelect() {
        val array = arrayOf("option 1", "option 2")
        var selectedOption: String? = null
        MaterialAlertDialogBuilder(this)
            .setTitle("Title")
            .setItems(array) { _, index ->
                selectedOption = array[index]
            }
            .setNegativeButton(R.string.button_cancel) { _, _ ->
            }
            .setOnDismissListener {
                selectedOption?.let {
                    Log.d(TAG, "selectedOption: $selectedOption")
                }
            }
            .show()
    }

    private fun popUpDialogConfirm() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Title")
            .setPositiveButton(R.string.button_confirm) { _, _ ->
                Log.d(TAG, "confirm")
            }
            .setNegativeButton(R.string.button_cancel) { _, _ ->
                Log.d(TAG, "cancel")
            }
            .show()
    }

    private fun setImage(image: File, uploadedPreview: AppCompatImageView) {
        Picasso.get()
            .load(image)
            .fit()
            .centerInside()
            .into(uploadedPreview)
    }

    override fun getLayoutResId(): Int = R.layout.activity_test

    override fun getViewModelInstance(): TestViewModel = TestViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }
}