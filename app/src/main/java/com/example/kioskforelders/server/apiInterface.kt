package com.example.kioskforelders.server

import com.example.kioskforelders.data.request.responseStart
import retrofit2.Call
import retrofit2.http.GET

interface apiInterface {
    @GET("/first")
    fun requestrUserId(): Call<responseStart>
}