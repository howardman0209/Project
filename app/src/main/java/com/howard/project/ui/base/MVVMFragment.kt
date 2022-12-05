package com.howard.project.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.howard.project.R

abstract class MVVMFragment<VM : BaseViewModel, BINDING : ViewDataBinding> : Fragment() {
    protected lateinit var viewModel: VM
    protected lateinit var binding: BINDING
    protected lateinit var mContext: Context
    private lateinit var baseActivity: BaseActivity

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModelInstance()

    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return setMainLayout(inflater, container)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inflate base layout
        LayoutInflater.from(context).inflate(R.layout.fragment_base, view.parent as? ViewGroup)
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?, skip: Boolean) {
        super.onViewCreated(view, savedInstanceState)

        if (!skip) LayoutInflater.from(context)
            .inflate(R.layout.fragment_base, view.parent as ViewGroup)
    }


    @CallSuper
    override fun onDestroy() {
        super.onDestroy()

    }


    private fun setMainLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)

        setBindingData()

        return binding.root
    }


    abstract fun getLayoutResId(): Int

    abstract fun getViewModelInstance(): VM

    abstract fun setBindingData()

    fun showLoadingIndicator(isShowLoading: Boolean) {
        baseActivity.showLoadingIndicator(isShowLoading)
    }

    // Prevent unintended double/ multiple click
    class PreventFastDoubleClick() {
        companion object {
            private var lastClickTime: Long = 0

            fun isFastDoubleClick(): Boolean {

                //取得現在時間
                var time = System.currentTimeMillis()
                //timeD :上次點擊時間與現在時間的時間差
                var timeD = time - lastClickTime
                return if (timeD in 1..499) {
                    //若小於0.5秒則判定是快速點擊
                    true
                } else {
                    //若大於0.5秒，把現在時間設為上次點擊時間
                    lastClickTime = time
                    false
                }
            }

        }
    }

    fun View.setSingleClickListener(action: () -> Unit) {
        setOnClickListener {
            if (!PreventFastDoubleClick.isFastDoubleClick()) {
                action.invoke()
            } else {
                Log.d("Click Event", "== ! == Click Prevented")
            }
        }
    }
}