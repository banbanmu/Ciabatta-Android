package com.example.risogelato.common

import android.app.Application
import com.example.risogelato.R
import com.example.risogelato.engine.AgoraEventHandler
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine

class BaseApplication : Application() {

    var rtcEngine: RtcEngine? = null
        private set

    private val agoraEventHandler = AgoraEventHandler()

    override fun onCreate() {
        super.onCreate()
        try {
            rtcEngine = RtcEngine.create(
                applicationContext,
                getString(R.string.agora_app_id),
                agoraEventHandler
            ).apply {
                setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
                setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
                enableVideo()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}