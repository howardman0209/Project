package com.howard.project.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.howard.project.R

abstract class BaseFragment : Fragment() {
    protected lateinit var baseActivity: BaseActivity
    protected lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity
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
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    open fun setMainLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getLayoutResId(), container, false)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()

    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun screenName(): String?

    open fun allowBackPress(): Boolean = true

    fun showLoadingIndicator(isShowLoading: Boolean) {
        baseActivity.showLoadingIndicator(isShowLoading)
    }

    // Prevent unintended double/ multiple click
    class PreventFastDoubleClick() {
        companion object {
            private var lastClickTime: Long = 0

            fun isFastDoubleClick(): Boolean {

                //??????????????????
                val time = System.currentTimeMillis()
                //timeD :?????????????????????????????????????????????
                val timeD = time - lastClickTime
                return if (timeD in 1..499) {
                    //?????????0.5???????????????????????????
                    true
                } else {
                    //?????????0.5?????????????????????????????????????????????
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