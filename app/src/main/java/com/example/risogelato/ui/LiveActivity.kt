package com.example.risogelato.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.example.risogelato.R
import com.example.risogelato.common.BaseApplication
import com.example.risogelato.data.remote.source.LiveDataSource
import com.example.risogelato.data.remote.source.LiveDataSourceImpl
import com.example.risogelato.data.remote.source.OrderDataSource
import com.example.risogelato.data.remote.source.OrderDataSourceImpl
import com.example.risogelato.domain.entity.*
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_live.*
import java.util.concurrent.TimeUnit

private const val STORE_INTENT_KEY = "STORE_INTENT_KEY"

class LiveActivity : AppCompatActivity() {

    private lateinit var liveDataSource: LiveDataSource
    private lateinit var orderDataSource: OrderDataSource

    private lateinit var store: Store

    private val disposables = CompositeDisposable()

    companion object {

        fun newIntent(context: Context, store: Store): Intent {
            return Intent(context, LiveActivity::class.java)
                .putExtra(STORE_INTENT_KEY, store)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        liveDataSource = LiveDataSourceImpl(this)
        orderDataSource = OrderDataSourceImpl(this)

        intent.getParcelableExtra<Store?>(STORE_INTENT_KEY)?.also {
            this.store = it
        } ?: run { finish() }

        initBroadcastingView()
        startLive()

        initBtnListeners()
        initOrderListToggle()
        loadOrderList()
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine()?.leaveChannel()
        disposables.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopLive()
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

    private fun initOrderListToggle() {
        toggle_order_list.setOnClickListener {
            if (box_order_list.translationY > 0) showOrderList()
            else hideOrderList()
        }
    }

    private fun showOrderList() {
        toggle_order_list.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_expand_more_black_18, 0)

        box_order_list.animate()
            .translationY(0f)
            .setInterpolator(LinearOutSlowInInterpolator())
            .setDuration(400)
            .start()
    }

    private fun hideOrderList() {
        toggle_order_list.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.outline_expand_less_black_18, 0)

        box_order_list.animate()
            .translationY(box_order_list_content.height.toFloat())
            .setInterpolator(LinearOutSlowInInterpolator())
            .setDuration(400)
            .start()
    }

    private fun loadOrderList() {
        Observable.fromCallable { orderDataSource.list() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ orders ->
                list_waiting.removeAllViews()
                list_started.removeAllViews()

                orders.forEach { order ->
                    val view = LayoutInflater.from(this).inflate(R.layout.view_order_item, null).apply {
                        setOnClickListener {
                            when(order.state) {
                                State.NOT_STARTED -> startOrder(order)
                                State.STARTED -> finishOrder(order)
                            }
                        }
                    }

                    when (order.state) {
                        State.NOT_STARTED -> list_waiting.addView(view)
                        State.STARTED -> list_started.addView(view)
                    }
                }
            }, Throwable::printStackTrace)
            .let { disposables.add(it) }
    }

    private fun startOrder(order: Order) {
        Observable.fromCallable { orderDataSource.start(order.id, store.id) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loadOrderList()
            }, Throwable::printStackTrace)
            .let { disposables.add(it) }
    }

    private fun finishOrder(order: Order) {
        Observable.fromCallable { orderDataSource.finish(order.id, store.id) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loadOrderList()
            }, Throwable::printStackTrace)
            .let { disposables.add(it) }
    }

    private fun getBaseApplication() = application as BaseApplication

    private fun rtcEngine() = getBaseApplication().rtcEngine
}
