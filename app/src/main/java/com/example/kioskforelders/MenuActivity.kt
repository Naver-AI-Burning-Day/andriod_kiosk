package com.example.kioskforelders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_menu.*

/** CSR에 사용되는 것들 */
import android.app.Activity
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.kioskforelders.csr.AudioWriterPCM
import com.example.kioskforelders.csr.NaverRecognizer
import com.naver.speech.clientapi.SpeechConfig.EndPointDetectType
import com.naver.speech.clientapi.SpeechRecognitionResult
import java.lang.ref.WeakReference
import java.util.List
/************************/

class MenuActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val CLIENT_ID = "0q5uu7pl8k"
    private var strResult: String? = null
    private var writer: AudioWriterPCM? = null
    private var currentEpdType: EndPointDetectType? = null
    private var isEpdTypeSelected: Boolean = false
    internal lateinit var handler: RecognitionHandler
    internal lateinit var naverRecognizer: NaverRecognizer

    /** 풀 스크린 만들기 변수 세팅 */
    lateinit var decorView: View
    var uiOption: Int = 0

    // Handle speech recognition Messages.
    private fun handleMessage(msg: Message) {
        when (msg.what) {
            R.id.clientReady -> {
                // Now an user can speak.
                //txtResult?.text = "Connected"
                writer = AudioWriterPCM(
                    Environment.getExternalStorageDirectory().absolutePath + "/NaverSpeechTest"
                )
                writer?.open("Test")
            }

            R.id.audioRecording -> writer?.write(msg.obj as ShortArray)

            R.id.partialResult -> {
                // Extract obj property typed with String.
                strResult = msg.obj as String
                //txtResult?.text = strResult
            }

            R.id.finalResult -> {
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                val speechRecognitionResult = msg.obj as SpeechRecognitionResult
                val results = speechRecognitionResult.results
                val strBuf = StringBuilder()
                for (result in results) {
                    strBuf.append(result)
                    strBuf.append("\n")
                }
                strResult = strBuf.toString()
                //txtResult?.text = strResult
            }

            R.id.recognitionError -> {
                writer?.close()

                strResult = "Error code : " + msg.obj.toString()
                //txtResult?.text = strResult
                //btnHybridRecognize?.setText(R.string.str_start)
                //btnHybridRecognize?.isEnabled = true
            }

            R.id.clientInactive -> {
                writer?.close()

                //btnHybridRecognize?.setText(R.string.str_start)
                //btnHybridRecognize?.isEnabled = true
            }

            R.id.endPointDetectTypeSelected -> {
                isEpdTypeSelected = true
                currentEpdType = msg.obj as EndPointDetectType
                if (currentEpdType === EndPointDetectType.AUTO) {
                    Toast.makeText(this, "AUTO epd type is selected.", Toast.LENGTH_SHORT).show()
                } else if (currentEpdType === EndPointDetectType.MANUAL) {
                    Toast.makeText(this, "MANUAL epd type is selected.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

//        layout_menuActivity_total.setOnClickListener {
//            val intent = Intent(this, Ordercheck::class.java)
//            startActivity(intent)
//        }

        handler = RecognitionHandler(this)
        naverRecognizer = NaverRecognizer(this, handler, CLIENT_ID)

        if (!naverRecognizer.getSpeechRecognizer().isRunning) {
            // Run SpeechRecongizer by calling recognize().
            strResult = ""
            //txtResult.text = "Connecting..."
            //btnStart.setText(R.string.str_stop)
            Log.d("chohee", "들어옴1")

            //currentEpdType = EndPointDetectType.HYBRID
            //isEpdTypeSelected = false
            naverRecognizer.recognize()
            Log.d("chohee", "들어옴2")
        }else{
            Log.d(TAG, "stop and wait Final Result")
            //btnStart.isEnabled = false
            naverRecognizer.getSpeechRecognizer().stop()
            //txtResult.text = "음성 인식 끝"
        }

    }

    override fun onStart() {
        super.onStart()
        naverRecognizer.getSpeechRecognizer().initialize()
    }

    override fun onResume() {
        super.onResume()
        deleteStatusBar()
        strResult = ""
        //txtResult?.text = ""
        //btnHybridRecognize?.setText(R.string.str_start)
        //btnHybridRecognize?.isEnabled = true
    }

    override fun onStop() {
        super.onStop()
        naverRecognizer.getSpeechRecognizer().release()
    }

    // SpeechRecognizer 클래스를 쓰레드로 만들고 이 쓰레드를 핸들링하는 핸들러를 정의하는 것
// Declare handler for handling SpeechRecognizer thread's Messages.
    internal class RecognitionHandler(activity: MenuActivity) : Handler() {
        private val mActivity: WeakReference<MenuActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            activity?.handleMessage(msg)
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
