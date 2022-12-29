package com.howard.project.ui.view

import android.content.Intent
import android.os.Bundle
import com.howard.project.R
import com.howard.project.databinding.ActivitySplashBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : MVVMActivity<SplashViewModel, ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
//        Log.d(LIFECYCLE, "SplashActivity onCreate")
        // Keep the splash screen visible for this Activity
//        splashScreen.setKeepOnScreenCondition { true }
//        startFirstActivity()
//        finish()


        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            val intent = Intent(applicationContext, ApduActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startFirstActivity() {
        startActivity(Intent(applicationContext, FirstActivity::class.java))
    }

    override fun getLayoutResId(): Int = R.layout.activity_splash

    override fun getViewModelInstance(): SplashViewModel = SplashViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }


}