package com.example.kioskforelders

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Ordersuccess : AppCompatActivity() {

    /** 풀 스크린 만들기 변수 세팅 */
    lateinit var decorView: View
    var uiOption: Int = 0

    override fun onResume() {
        super.onResume()
        deleteStatusBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordersuccess)
    }

    /** 풀 스크린 만들기 */
    private fun deleteStatusBar() {
        decorView = window.decorView
        uiOption = decorView.getSystemUiVisibility()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption = uiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption = uiOption or View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption = uiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.setSystemUiVisibility(uiOption)
    }
}