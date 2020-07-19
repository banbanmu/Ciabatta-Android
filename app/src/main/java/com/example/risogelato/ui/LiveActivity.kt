package com.example.risogelato.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.risogelato.R
import com.example.risogelato.common.BaseApplication
import com.example.risogelato.data.remote.source.LiveDataSource
import com.example.risogelato.data.remote.source.LiveDataSourceImpl
import com.example.risogelato.domain.entity.ClipInfo
import com.example.risogelato.domain.entity.ClipInfoList
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_live.*
import java.util.concurrent.TimeUnit

class LiveActivity : AppCompatActivity() {

    private lateinit var liveDataSource: LiveDataSource

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        liveDataSource = LiveDataSourceImpl(this)

        initBroadcastingView()
        startLive()

        initBtnListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine()?.leaveChannel()
        disposables.clear()
    }

    private fun initBroadcastingView() {
        RtcEngine.CreateRendererView(this).run {
            setZOrderMediaOverlay(true)
            this@LiveActivity.box_live_container.addView(this)

            val canvas = VideoCanvas(this, VideoCanvas.RENDER_MODE_HIDDEN, 0)
            rtcEngine()?.setupLocalVideo(canvas)
        }
    }

    private fun initBtnListeners() {
        btn_stop_live.setOnClickListener {
            stopLive()
        }
    }

    private fun startLive() {
        Observable.fromCallable { liveDataSource.start() }
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                joinChannel(it.channelName, it.uid)
            }, Throwable::printStackTrace)
            .let { disposables.add(it) }
    }

    private fun joinChannel(channelName: String, uid: Int) {
        rtcEngine()?.joinChannel(null, channelName, null, uid)
    }

    private fun stopLive() {
        val clipInfoList = ClipInfoList(listOf(
            ClipInfo("제육볶음", 3000, 5000),
            ClipInfo("오징어볶음", 10000, 3000)
        ))
        Observable.fromCallable { liveDataSource.stop(clipInfoList) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                finish()
            }, {
                it.printStackTrace()
                finish()
            })
            .let { disposables.add(it) }
    }

    private fun getBaseApplication() = application as BaseApplication

    private fun rtcEngine() = getBaseApplication().rtcEngine
}
