package com.howard.project.ui.view

import com.howard.project.R
import com.howard.project.databinding.FragmentMoreBinding
import com.howard.project.ui.base.MVVMFragment
import com.howard.project.ui.viewModel.MoreViewModel

class MoreFragment: MVVMFragment<MoreViewModel, FragmentMoreBinding>() {

    override fun getViewModelInstance(): MoreViewModel = MoreViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

    override fun getLayoutResId(): Int = R.layout.fragment_more
}