package com.howard.project.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {
    const val BASE_URL = "https://us-central1-project-1-723a3.cloudfunctions.net/"
    inline fun <reified T> create(baseUrl: String = BASE_URL): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(T::class.java)
    }
}