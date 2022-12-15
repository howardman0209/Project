package com.howard.project.ui.viewModel

import android.util.Log
import com.howard.project.extension.TAG
import com.howard.project.network.ApiManager
import com.howard.project.network.GatewayService
import com.howard.project.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TestViewModel : BaseViewModel() {

    fun testingApi() {
        showLoadingIndicator(true)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        disposableList.add(
            ApiManager.create<GatewayService>().addMessage(
                text = "Hello World at $formatted"
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