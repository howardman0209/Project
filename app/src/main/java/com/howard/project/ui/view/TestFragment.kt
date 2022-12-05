package com.howard.project.ui.view

import com.howard.project.R
import com.howard.project.databinding.FragmentTestBinding
import com.howard.project.ui.base.MVVMFragment
import com.howard.project.ui.viewModel.TestViewModel

class TestFragment  : MVVMFragment<TestViewModel, FragmentTestBinding>() {

    override fun getViewModelInstance(): TestViewModel = TestViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

    override fun getLayoutResId(): Int = R.layout.fragment_test
}