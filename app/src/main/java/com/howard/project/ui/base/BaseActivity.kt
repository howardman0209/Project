package com.howard.project.ui.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.howard.project.R
import com.howard.project.extension.LIFECYCLE
import com.howard.project.extension.TAG
import com.howard.project.ui.model.PermissionRequest
import com.howard.project.ui.model.PermissionResult
import com.howard.project.uiComponent.ProgressDialog


abstract class BaseActivity : AppCompatActivity() {
    private val permCallbackMap = mutableMapOf<Int, PermissionResult.() -> Unit>()
    lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "BaseActivity onCreate")

        setMainLayout()
        layoutInflater.inflate(R.layout.activity_base, findViewById(android.R.id.content))

        progressDialog = ProgressDialog(this).create()


//        viewModel.isShowLoading.observe(this) {
//            showLoadingIndicator(it == true)
//        }

    }

    open fun setMainLayout() {
        setContentView(getLayoutResId())
    }


    inline fun requirePermissions(
        permissions: Array<out String>,
        requestBlock: PermissionRequest.() -> Unit
    ) {
        val permRequest = PermissionRequest().apply(requestBlock)
        requireNotNull(permRequest.requestCode) { "No requestCode specified." }
        requireNotNull(permRequest.resultCallback) { "No callback specified." }

        dispatchPermissionCheck(
            permissions,
            permRequest.requestCode!!,
            permRequest.resultCallback!!
        )
    }

    @PublishedApi
    internal fun dispatchPermissionCheck(
        permissions: Array<out String>,
        requestCode: Int,
        callback: PermissionResult.() -> Unit
    ) {
        permCallbackMap[requestCode] = callback
        val notGranted = permissions
            .filter {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }
            .toTypedArray()

        when {
            notGranted.isEmpty() -> onPermissionResult(
                PermissionResult.PermissionGranted(
                    requestCode
                )
            )
            notGranted.any { shouldShowRequestPermissionRationale(it) } -> onPermissionResult(
                PermissionResult.NeedRationale(requestCode)
            )
            else -> requestPermissions(notGranted, requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when {
            grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED } -> {
                // all perms granted after the result back
                onPermissionResult(PermissionResult.PermissionGranted(requestCode))
            }
            permissions.any { shouldShowRequestPermissionRationale(it) } -> {
                // some denied & rationale might be needed
                onPermissionResult(PermissionResult.PermissionDenied(requestCode))
            }
            else -> {
                // repeatedly denied the permission -> os regards this as permanently denied
                // need to prompt user to app settings to change it
                onPermissionResult(PermissionResult.PermissionDeniedPermanently(requestCode))
            }
        }
    }

    private fun onPermissionResult(permissionResult: PermissionResult) {
        permCallbackMap[permissionResult.requestCode]?.let {
            permissionResult.it()
        }
        permCallbackMap.remove(permissionResult.requestCode)
    }

    fun startAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", applicationContext.packageName, null)
                it.data = uri
                startActivity(it)
            }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    v.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun showLoadingIndicator(isShowLoading: Boolean) {
        if (isShowLoading) {
            if (::progressDialog.isInitialized) {
                progressDialog.show()
            }
        } else {
            if (::progressDialog.isInitialized) {
                progressDialog.dismiss()
            }
        }
    }

    fun loadingIndicatorState(): Boolean {
        Log.d(TAG, "progressDialog.isInitialized: ${::progressDialog.isInitialized}")
        return if (::progressDialog.isInitialized) {
            progressDialog.isShowing
        } else {
            false
        }
    }

    enum class PushFragmentAction {
        Add,
        Replace
    }

    fun pushFragment(
        fragment: Fragment,
        @IdRes containerId: Int,
        action: PushFragmentAction = PushFragmentAction.Replace,
        isAddToBackStack: Boolean = true
    ) {
        Log.d("DEBUG", "pushFragment")
        supportFragmentManager.beginTransaction().apply {
            if (action == PushFragmentAction.Replace) {
                replace(containerId, fragment)
            } else {
                add(containerId, fragment)
            }
            if (isAddToBackStack) {
                addToBackStack(fragment.javaClass.name)
            }
            commit()
        }
    }

    // Prevent unintended double/ multiple click
    class PreventFastDoubleClick() {
        companion object {
            private var lastClickTime: Long = 0

            fun isFastDoubleClick(): Boolean {

                //取得現在時間
                var time = System.currentTimeMillis()
                //timeD :上次點擊時間與現在時間的時間差
                var timeD = time - lastClickTime
                return if (timeD in 1..499) {
                    //若小於0.5秒則判定是快速點擊
                    true
                } else {
                    //若大於0.5秒，把現在時間設為上次點擊時間
                    lastClickTime = time
                    false
                }
            }

        }
    }

    fun View.setSingleClickListener(a: () -> Unit) {
        setOnClickListener {
            if (!PreventFastDoubleClick.isFastDoubleClick()) {
                a.invoke()
            } else {
                Log.d("Click Event", "== ! == Click Prevented")
            }
        }
    }

    abstract fun getLayoutResId(): Int
}