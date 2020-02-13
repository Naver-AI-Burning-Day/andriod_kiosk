package com.example.kioskforelders.server

import com.example.kioskforelders.data.response.responseStart
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface apiInterface {
    // userId 요청
    @GET("/first")
    fun requestrUserId(): Call<responseStart>

    // CSR 추출 텍스트 보내기
//    @POST("/send")
//    fun requestOrder(@Body userId: requestOrder)
}