package com.howard.project.ui.view

import android.os.Bundle
import android.util.Log
import com.howard.project.R
import com.howard.project.databinding.ActivityTestBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.model.FcmRedirectData
import com.howard.project.ui.viewModel.TestViewModel
import com.howard.project.util.BUNDLE_FCM_DATA

class TestActivity : MVVMActivity<TestViewModel, ActivityTestBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fcmData = intent?.getSerializableExtra(BUNDLE_FCM_DATA) as? FcmRedirectData
        Log.d("TestActivity", "fcmData: $fcmData")

    }

    override fun getLayoutResId(): Int = R.layout.activity_test

    override fun getViewModelInstance(): TestViewModel = TestViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }

}