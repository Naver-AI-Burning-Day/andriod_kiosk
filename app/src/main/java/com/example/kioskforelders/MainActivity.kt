package com.example.kioskforelders


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.kioskforelders.data.response.responseStart
import com.example.kioskforelders.server.ServiceInplement
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    /** 풀 스크린 만들기 변수 세팅 */
    lateinit var decorView: View
    var uiOption: Int = 0

    override fun onResume() {
        super.onResume()
        deleteStatusBar() // onCreate 전에 풀 스크린 세팅하기

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** 화면 터치시 메뉴 화면으로 넘기기 */
        layout_mainActivity_total.setOnClickListener {
            /** CSR로 추출한 텍스트 서버로 보내기 */
            val call: Call<responseStart> = ServiceInplement.service.requestrUserId()
            call.enqueue(
                object : Callback<responseStart> {
                    override fun onFailure(call: Call<responseStart>, t: Throwable) {
                        Log.d("서버 통신 성공 여부" , "실패")
                    }

                    override fun onResponse(
                        call: Call<responseStart>,
                        response: Response<responseStart>
                    ) {
                        Log.d("서버 통신 성공 여부" , "성공!")
                        Log.d("chohee", response.body()?.id.toString())
                    }
                }
            )
            var intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

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
