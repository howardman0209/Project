package com.howard.project.ui.viewModel

import android.util.Log
import com.howard.project.extension.TAG
import com.howard.project.network.ApiManager
import com.howard.project.network.GatewayService
import com.howard.project.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TestViewModel : BaseViewModel() {

    fun testingApi() {
        showLoadingIndicator(true)
        disposableList.add(
            ApiManager.create<GatewayService>().addMessage(
                text = "Hello World at ${getCurrentTime()}"
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { showLoadingIndicator(false) }
                .subscribe({
                    Log.d(TAG, "ApiResponse: ${it.body()}")
                }, {
                    throw it
                })
        )
    }
}