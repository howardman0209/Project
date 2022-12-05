package com.howard.project.ui.view

import com.howard.project.R
import com.howard.project.databinding.FragmentHomeBinding
import com.howard.project.ui.base.MVVMFragment
import com.howard.project.ui.viewModel.HomeViewModel


class HomeFragment: MVVMFragment<HomeViewModel, FragmentHomeBinding>() {

    override fun getViewModelInstance(): HomeViewModel = HomeViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
        binding.view = this
    }

    override fun getLayoutResId(): Int = R.layout.fragment_home
}