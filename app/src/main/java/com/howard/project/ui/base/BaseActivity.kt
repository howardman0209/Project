package com.howard.project.ui.base

import android.content.ClipData
import android.content.ClipboardManager
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
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.howard.project.R
import com.howard.project.extension.TAG
import com.howard.project.ui.model.PermissionRequest
import com.howard.project.ui.model.PermissionResult
import com.howard.project.ui.view.LoginActivity
import com.howard.project.uiComponent.ProgressDialog
import com.howard.project.util.LoginManager
import io.reactivex.disposables.Disposable


abstract class BaseActivity : LocalizationActivity() {
    private val permCallbackMap = mutableMapOf<Int, PermissionResult.() -> Unit>()
    lateinit var progressDialog: AlertDialog
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d(LIFECYCLE, "BaseActivity onCreate")

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

    @LayoutRes
    abstract fun getLayoutResId(): Int

    @IdRes
    open fun getMainFragmentContainer(): Int? = null

    @CallSuper
    override fun onDestroy() {
        progressDialog.dismiss()

        if (disposable?.isDisposed == false) {
            disposable?.dispose()
        }
        permCallbackMap.clear()
        super.onDestroy()
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

    open fun handleApiError(error: String) {
        showNonDisruptiveApiError(error)
    }

    private fun showNonDisruptiveApiError(message: String) {
        showLoadingIndicator(false)
        Snackbar.make(findViewById(R.id.baseActivityViewGroup), message, Snackbar.LENGTH_LONG).show()
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

    fun backToLogin() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.label_logout)
            .setPositiveButton(R.string.button_confirm) { _, _ ->
                LoginManager.clearUserSession()
                Firebase.auth.signOut()

                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun copyToClipboard(textToCopy: String, label: String? = "CopyContent") {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    fun isLoggedIn() = LoginManager.user != null

    fun accountType() = getString(R.string.account_type)

    fun appName() = getString(R.string.app_name)

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
//        Log.d("DEBUG", "pushFragment")
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
    class PreventFastDoubleClick {
        companion object {
            private var lastClickTime: Long = 0

            fun isFastDoubleClick(): Boolean {

                //??????????????????
                val time = System.currentTimeMillis()
                //timeD :?????????????????????????????????????????????
                val timeD = time - lastClickTime
                return if (timeD in 1..499) {
                    //?????????0.5???????????????????????????
                    true
                } else {
                    //?????????0.5?????????????????????????????????????????????
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
}