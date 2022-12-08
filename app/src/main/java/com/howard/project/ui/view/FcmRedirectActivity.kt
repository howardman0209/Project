package com.howard.project.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.howard.project.R
import com.howard.project.databinding.ActivityFcmRedirectBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.model.FcmRedirectData
import com.howard.project.ui.model.FcmRedirectData.NotificationType
import com.howard.project.ui.viewModel.FcmRedirectViewModel
import com.howard.project.util.BUNDLE_FCM_DATA
import com.howard.project.util.BUNDLE_FCM_NOTIFICATION_TYPE
import org.json.JSONObject


class FcmRedirectActivity : MVVMActivity<FcmRedirectViewModel, ActivityFcmRedirectBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent?.getStringExtra(BUNDLE_FCM_DATA)
        val notificationType = intent?.getSerializableExtra(BUNDLE_FCM_NOTIFICATION_TYPE) as NotificationType
        Log.d("FcmRedirectActivity", "data: $data")
        Log.d("FcmRedirectActivity", "notificationType: $notificationType")

        val intent = processRedirectIntent(notificationType, data)

        intent?.let {
            if (doRedirect()) {
                Log.d("FcmRedirectActivity", "do redirect")
                startActivity(it)
            }
        }

        finish()
    }

    private fun doRedirect(): Boolean {
        // TODO: Filter out some activity should not do redirect eg. during a game, during payment
        return true
    }

    private fun processRedirectIntent(notificationType: NotificationType, data: String?): Intent? {
        return try {
            val dataJsonObject = data?.let { JSONObject(it) }
            when (notificationType) {
                NotificationType.TEST -> {
                    val testDataMessage = dataJsonObject?.optString("testDataMessage")

                    // TODO: Logged in with different redirect action
                    Log.d("FcmRedirectActivity", "isLoggedIn: ${isLoggedIn()}")
                    if (isLoggedIn()) {
                        val testData = FcmRedirectData.TestData(
                            testString = "FCM Redirect Test Data: $testDataMessage"
                        )
                        Intent(applicationContext, TestActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            putExtra(BUNDLE_FCM_DATA, testData)
                        }
                    } else {
                        val testData = FcmRedirectData.TestData(
                            testString = "FCM Redirect Test Data: $testDataMessage"
                        )
                        Intent(applicationContext, SplashActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            putExtra(BUNDLE_FCM_DATA, testData)
                        }
                    }
                }
                NotificationType.UNKNOWN -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_fcm_redirect

    override fun getViewModelInstance(): FcmRedirectViewModel = FcmRedirectViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }

}