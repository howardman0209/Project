package com.howard.project.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.howard.project.extension.LIFECYCLE
import com.howard.project.uiComponent.ProgressDialog

abstract class MVVMActivity<VM : BaseViewModel, BINDING : ViewDataBinding> : BaseActivity() {
    protected lateinit var viewModel: VM
    protected lateinit var binding: BINDING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE, "MVVMActivity onCreate")

        viewModel = getViewModelInstance()

        setBindingData()

//        progressDialog = ProgressDialog(this).create()


//        viewModel.isShowLoading.observe(this) {
//            showLoadingIndicator(it == true)
//        }


    }

    override fun setMainLayout() {
        binding = DataBindingUtil.setContentView(this, getLayoutResId())
    }

    abstract fun getViewModelInstance(): VM

    abstract fun setBindingData()

}