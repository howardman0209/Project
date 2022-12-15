package com.howard.project.network

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Endpoint
const val addMessagePath = "addMessage"

// Contract
interface GatewayService {
    @GET(addMessagePath)
    fun addMessage(@Query("text") text: String): Observable<Response<Any>>
}