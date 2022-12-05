package com.howard.project.ui.view

import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.howard.project.R
import com.howard.project.databinding.ActivityFirstBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.FirstViewModel

class FirstActivity : MVVMActivity<FirstViewModel, ActivityFirstBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DEBUG", "Hello World in FirstActivity")
        viewModel.text.set("Hello World")
    }

    override fun getLayoutResId(): Int = R.layout.activity_first

    override fun getViewModelInstance(): FirstViewModel = FirstViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }

}