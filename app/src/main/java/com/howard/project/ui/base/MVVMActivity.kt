package com.howard.project.ui.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class MVVMActivity<VM : BaseViewModel, BINDING : ViewDataBinding> : BaseActivity() {
    protected lateinit var viewModel: VM
    protected lateinit var binding: BINDING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d(LIFECYCLE, "MVVMActivity onCreate")

        viewModel = getViewModelInstance()

        setBindingData()

//        progressDialog = ProgressDialog(this).create()


//        viewModel.isShowLoading.observe(this) {
//            showLoadingIndicator(it == true)
//        }


    }

    @CallSuper
    override fun onDestroy() {
        /**
         * Dispose all disposables
         */
        viewModel.getDisposableList().forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }

        super.onDestroy()
    }

    override fun setMainLayout() {
        binding = DataBindingUtil.setContentView(this, getLayoutResId())
    }

    abstract fun getViewModelInstance(): VM

    abstract fun setBindingData()

}