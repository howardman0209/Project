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
        disposableList.add(
            ApiManager.create<GatewayService>().addMessage(
                text = "Hello World 14/12/22 17:29"
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "Response: $it")
                }, {
                    throw it
                })
        )
    }
}