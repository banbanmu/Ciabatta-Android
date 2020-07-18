package com.example.risogelato.engine

import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.IRtcEngineEventHandler.*

class AgoraEventHandler : IRtcEngineEventHandler() {

    private val handlers = mutableListOf<EventHandler>()

    fun addHandler(handler: EventHandler) {
        handlers.add(handler)
    }

    fun removeHandler(handler: EventHandler?) {
        handlers.remove(handler)
    }

    override fun onJoinChannelSuccess(
        channel: String,
        uid: Int,
        elapsed: Int
    ) {
        handlers.forEach { it.onJoinChannelSuccess(channel, uid, elapsed) }
    }

    override fun onLeaveChannel(stats: RtcStats) {
        handlers.forEach { it.onLeaveChannel(stats) }
    }

    override fun onFirstRemoteVideoDecoded(
        uid: Int,
        width: Int,
        height: Int,
        elapsed: Int
    ) {
        handlers.forEach { it.onFirstRemoteVideoDecoded(uid, width, height, elapsed) }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        handlers.forEach { it.onUserJoined(uid, elapsed) }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        handlers.forEach { it.onUserOffline(uid, reason) }
    }

    override fun onLocalVideoStats(stats: LocalVideoStats) {
        handlers.forEach { it.onLocalVideoStats(stats) }
    }

    override fun onRtcStats(stats: RtcStats) {
        handlers.forEach { it.onRtcStats(stats) }
    }

    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        handlers.forEach { it.onNetworkQuality(uid, txQuality, rxQuality) }
    }

    override fun onRemoteVideoStats(stats: RemoteVideoStats) {
        handlers.forEach { it.onRemoteVideoStats(stats) }
    }

    override fun onRemoteAudioStats(stats: RemoteAudioStats) {
        handlers.forEach { it.onRemoteAudioStats(stats) }
    }

    override fun onLastmileQuality(quality: Int) {
        handlers.forEach { it.onLastmileQuality(quality) }
    }

    override fun onLastmileProbeResult(result: LastmileProbeResult) {
        handlers.forEach { it.onLastmileProbeResult(result) }
    }
}

interface EventHandler {
    fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int)
    fun onLeaveChannel(stats: RtcStats?)
    fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)
    fun onUserOffline(uid: Int, reason: Int)
    fun onUserJoined(uid: Int, elapsed: Int)
    fun onLastmileQuality(quality: Int)
    fun onLastmileProbeResult(result: LastmileProbeResult?)
    fun onLocalVideoStats(stats: LocalVideoStats?)
    fun onRtcStats(stats: RtcStats?)
    fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int)
    fun onRemoteVideoStats(stats: RemoteVideoStats?)
    fun onRemoteAudioStats(stats: RemoteAudioStats?)
}