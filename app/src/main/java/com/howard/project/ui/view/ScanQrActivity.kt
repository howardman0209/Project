package com.howard.project.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.howard.project.R
import com.howard.project.databinding.ActivityScanQrBinding
import com.howard.project.ui.base.BaseActivity
import com.howard.project.util.BUNDLE_CAMERA_SCANNER_RESULT

class ScanQrActivity : BaseActivity() {

    private lateinit var binding: ActivityScanQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = CameraScannerFragment.newInstance().apply {
            setListener(object : CameraScannerListener {
                override fun onScannedResult(data: String?) {
                    setResult(Activity.RESULT_OK, Intent().apply { putExtra(BUNDLE_CAMERA_SCANNER_RESULT, data) })
                    finish()
                }

            })
            setGalleryBtn(true)
        }
        pushFragment(fragment, R.id.fragmentContainer, isAddToBackStack = false)

    }

    override fun getLayoutResId(): Int = R.layout.activity_scan_qr

}