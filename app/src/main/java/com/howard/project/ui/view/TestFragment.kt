package com.howard.project.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.howard.project.R
import com.howard.project.databinding.FragmentTestBinding
import com.howard.project.extension.TAG
import com.howard.project.ui.base.MVVMFragment
import com.howard.project.ui.viewModel.TestViewModel

class TestFragment : MVVMFragment<TestViewModel, FragmentTestBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "TestFragment onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.testButton.setOnClickListener {
            startActivity(Intent(requireContext(), TestActivity::class.java))
        }
    }

    override fun getViewModelInstance(): TestViewModel = TestViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

    override fun getLayoutResId(): Int = R.layout.fragment_test

    override fun screenName(): String = "TestFragment"
}