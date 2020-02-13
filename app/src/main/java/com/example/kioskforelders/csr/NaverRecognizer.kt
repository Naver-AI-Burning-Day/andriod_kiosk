package com.example.kioskforelders.csr

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.kioskforelders.R
import com.naver.speech.clientapi.SpeechConfig
import com.naver.speech.clientapi.SpeechConfig.EndPointDetectType
import com.naver.speech.clientapi.SpeechConfig.LanguageType
import com.naver.speech.clientapi.SpeechRecognitionException
import com.naver.speech.clientapi.SpeechRecognitionListener
import com.naver.speech.clientapi.SpeechRecognitionResult
import com.naver.speech.clientapi.SpeechRecognizer
//import retrofit2.Retrofit


class NaverRecognizer : SpeechRecognitionListener{

    private val TAG = NaverRecognizer::class.java.simpleName
    private var mHandler: Handler? = null
    lateinit var mRecognizer: SpeechRecognizer
    lateinit var csrResult: String

    constructor(context: Context, handler: Handler, clientId: String) {
        this.mHandler = handler

        try {
            mRecognizer = SpeechRecognizer(context, clientId)
        } catch (e: SpeechRecognitionException) {
            // 예외가 발생하는 경우는 아래와 같습니다.
            //   1. activity 파라미터가 올바른 MainActivity의 인스턴스가 아닙니다.
            //   2. AndroidManifest.xml에서 package를 올바르게 등록하지 않았습니다.
            //   3. package를 올바르게 등록했지만 과도하게 긴 경우, 256바이트 이하면 좋습니다.
            //   4. clientId가 null인 경우
            //
            // 개발하면서 예외가 발생하지 않았다면 실서비스에서도 예외는 발생하지 않습니다.
            // 개발 초기에만 주의하시면 됩니다.
            e.printStackTrace()
        }

        mRecognizer.setSpeechRecognitionListener(this)
    }


    fun getSpeechRecognizer(): SpeechRecognizer {
        return mRecognizer
    }

    fun recognize() {
        try {
            mRecognizer.recognize(
                SpeechConfig(
                    LanguageType.KOREAN,
                    EndPointDetectType.HYBRID
                )
            )
        } catch (e: SpeechRecognitionException) {
            e.printStackTrace()
        }

    }

    @WorkerThread
    override fun onResult(finalResult: SpeechRecognitionResult?) {
        Log.d(TAG, "Final Result!! (" + finalResult?.results?.get(0) + ")")
        val msg = Message.obtain(mHandler, R.id.finalResult, finalResult)
        msg.sendToTarget()
        csrResult = finalResult?.results?.get(0).toString()

    }

    @WorkerThread
    override fun onReady() {
        Log.d(TAG, "Event occurred : Ready")
        val msg = Message.obtain(mHandler, R.id.clientReady)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected")
    }

    @WorkerThread
    override fun onPartialResult(partialResult: String?) {
        Log.d(TAG, "Partial Result!! ($partialResult)")
        val msg = Message.obtain(mHandler, R.id.partialResult, partialResult)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onInactive() {
        Log.d(TAG, "Event occurred : Inactive")
        val msg = Message.obtain(mHandler, R.id.clientInactive)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onRecord(speech: ShortArray?) {
        Log.d(TAG, "Event occurred : Record")
        val msg = Message.obtain(mHandler, R.id.audioRecording, speech)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onError(errorCode: Int) {
        Log.d(TAG, "Error!! (" + Integer.toString(errorCode) + ")")
        val msg = Message.obtain(mHandler, R.id.recognitionError, errorCode)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetectTypeSelected(epdType: EndPointDetectType?) {
        Log.d(TAG, "EndPointDetectType is selected!! (" + epdType?.toInteger()?.let {
            Integer.toString(
                it
            )
        } + ")")
        val msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType)
        msg.sendToTarget()
    }

}