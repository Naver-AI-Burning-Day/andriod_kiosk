package com.example.kioskforelders

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.kioskforelders.data.request.requestFinal
import com.example.kioskforelders.data.request.requestMP3
import com.example.kioskforelders.data.response.responseFinal
import com.example.kioskforelders.data.response.responseMP3
import com.example.kioskforelders.server.ServiceImplement
import com.example.kioskforelders.server.SingletonData
import com.example.kioskforelders.server.SingletonData.mediaPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import android.media.AudioAttributes
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class OrdercheckActivity : AppCompatActivity() {

    /** 풀 스크린 만들기 변수 세팅 */
    lateinit var decorView: View
    var uiOption: Int = 0

    override fun onResume() {
        super.onResume()
        deleteStatusBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordercheck)

        // 주문 확인 UI 데이터 세팅







        /** 주문 확인 요청 (서버 통신) */
        val call: Call<responseFinal> = ServiceImplement.service.requestFinal(requestFinal(SingletonData.userId) )
        call.enqueue(
            object : Callback<responseFinal> {
                override fun onFailure(call: Call<responseFinal>, t: Throwable) {
                    Log.d("서버 통신 성공 여부" , "실패")
                }

                override fun onResponse(
                    call: Call<responseFinal>,
                    response: Response<responseFinal>
                ) {
                    Log.d("서버 통신 성공 여부" , "성공!")
                    /** 주문 확인 CPV 요청 (서버 요청) */
                    Log.d("MP3서버" , response.body()?.text)
                    val call: Call<responseMP3> = ServiceImplement.service.requestMP3(requestMP3(response.body()?.text))
                    call.enqueue(
                        object : Callback<responseMP3> {
                            override fun onFailure(call: Call<responseMP3>, t: Throwable) {
                                Log.d("MP3서버" , "실패")
                            }

                            override fun onResponse(
                                call: Call<responseMP3>,
                                response: Response<responseMP3>
                            ) {
                                initializeMediaPlayer(response.body()?.link.toString())
//                                val uri: Uri = Uri.parse(response.body()?.link)
//                                try {
//                                    mediaPlayer.reset()
//                                    mediaPlayer.setDataSource(this@OrdercheckActivity, uri)
//                                    //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
//                                    mediaPlayer.prepare() //don't use prepareAsync for mp3 playback
//                                    mediaPlayer.start()
//                                } catch (e: IOException) {
//                                    e.printStackTrace()
//                                }
                            }
                        }
                    )
                }
            }
        )

    }

    private fun initializeMediaPlayer(url: String) {
        Thread(Runnable{
            val player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            try {
                //change with setDataSource(Context,Uri);
                player.reset()
                player.setDataSource(this, Uri.parse(url))
                //player.setDisplay(surfaceHolder)
                player.prepareAsync()
                player.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                    //mp.start();
                    player.start()
                })
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })


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
