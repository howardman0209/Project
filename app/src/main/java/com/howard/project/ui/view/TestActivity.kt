package com.howard.project.ui.view

import android.os.Bundle
import android.util.Log
import com.howard.project.R
import com.howard.project.databinding.ActivityTestBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.TestViewModel

class TestActivity : MVVMActivity<TestViewModel, ActivityTestBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("DEBUG", "Hello World in TestActivity")
    }

    override fun getLayoutResId(): Int = R.layout.activity_test

    override fun getViewModelInstance(): TestViewModel = TestViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }

}