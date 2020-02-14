package com.example.kioskforelders.server

import android.media.MediaPlayer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SingletonData {

    // 유저 아이디 값
    var userId: Int? = -1
    val mediaPlayer = MediaPlayer()

}