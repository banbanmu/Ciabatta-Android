package com.example.risogelato.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.risogelato.R
import com.example.risogelato.common.BaseApplication
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.activity_live.*

class LiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        initBroadcastingView()
        joinChannel()
    }

    private fun initBroadcastingView() {
        RtcEngine.CreateRendererView(this).run {
            setZOrderMediaOverlay(true)
            this@LiveActivity.box_live_container.addView(this)

            val canvas = VideoCanvas(this, VideoCanvas.RENDER_MODE_HIDDEN, 0)
            rtcEngine()?.setupLocalVideo(canvas)
        }
    }

    private fun joinChannel() {
        rtcEngine()?.joinChannel(null, "afknle", null, 0)
    }

    private fun getBaseApplication() = application as BaseApplication

    private fun rtcEngine() = getBaseApplication().rtcEngine
}