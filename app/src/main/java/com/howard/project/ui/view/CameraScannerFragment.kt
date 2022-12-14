package com.howard.project.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.google.zxing.integration.android.IntentIntegrator
import com.howard.project.R
import com.howard.project.databinding.FragmentCameraScannerBinding
import com.howard.project.extension.requireFilePermission
import com.howard.project.ui.base.BaseActivity
import com.howard.project.ui.base.BaseFragment
import com.howard.project.util.BUNDLE_CAMERA_SCANNER_RESULT
import com.howard.project.util.CameraScannerDecodeUtil
import com.howard.project.util.GalleryImageUtil
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CameraScannerFragment : BaseFragment() {
    private var _binding: FragmentCameraScannerBinding? = null
    internal val binding get() = _binding!!

    private var listener: CameraScannerListener? = null
    private var capture: CaptureManager? = null
    private var beepManager: BeepManager? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult

        if (result.resultCode == Activity.RESULT_OK) {
            val contentResolver = activity?.contentResolver
            contentResolver?.let {
                val qr = CameraScannerDecodeUtil.scanCodeImage(GalleryImageUtil.getPictureBitmap(it, result.data?.data))
                if (qr == null) {
                    Snackbar.make(binding.root, getString(R.string.label_decode_image_fail), Snackbar.LENGTH_LONG).show()
                } else {
                    if (activity is ScanQrActivity) {
                        activity?.setResult(Activity.RESULT_OK, Intent().apply { putExtra(BUNDLE_CAMERA_SCANNER_RESULT, qr) })
                        activity?.finish()
                    }
                }
            }
            if (contentResolver == null) {
                Snackbar.make(binding.root, getString(R.string.label_decode_image_fail), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance() = CameraScannerFragment()
        private var isGalleryBtnOn = false
    }

    fun setListener(listener: CameraScannerListener) {
        this.listener = listener
    }

    fun setGalleryBtn(boolean: Boolean) {
        isGalleryBtnOn = boolean
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentCameraScannerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGallery.visibility = if (isGalleryBtnOn) View.VISIBLE else View.GONE

        capture = CaptureManager(activity, binding.zxingBarcodeScanner)
        val integrator = IntentIntegrator(activity)
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setCameraId(0)  // Use a specific camera of the device
        integrator.setBarcodeImageEnabled(true)
        capture?.initializeFromIntent(
            integrator.createScanIntent().apply {
                putExtra("SCAN_CAMERA_ID", 0)
            },
            savedInstanceState
        )

        beepManager = BeepManager(activity)

        //Model like SamSung S21 need to explicitly set isContinuousFocusEnabled to true.
        binding.zxingBarcodeScanner.barcodeView.cameraSettings.isContinuousFocusEnabled = true

        capture?.decode()

        binding.zxingBarcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                capture?.onPause()
                beepManager?.playBeepSoundAndVibrate()
                listener?.onScannedResult(result?.text)
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            }
        })

        binding.buttonGallery.setOnClickListener {
            (activity as BaseActivity).requireFilePermission(it) {
                pickImageLauncher.launch(
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                )
            }
        }
    }

    fun resumeScanner(needDelay: Boolean) {
        if (needDelay) {
            capture?.onResume()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                capture?.onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, "onResume")
        capture?.onResume()
    }

    override fun onPause() {
        super.onPause()
//        Log.d(TAG, "onPause")
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
    }

    override fun getLayoutResId(): Int = R.layout.fragment_camera_scanner

    override fun screenName(): String = "Master Camera Scanner"
}

interface CameraScannerListener {
    fun onScannedResult(data: String?)
}