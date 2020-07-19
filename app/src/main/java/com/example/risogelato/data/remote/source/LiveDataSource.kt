package com.example.risogelato.data.remote.source

import android.content.Context
import com.example.risogelato.common.OkHttpUtil
import com.example.risogelato.data.remote.RemoteExecutor
import com.example.risogelato.data.remote.RetrofitFactory
import com.example.risogelato.data.remote.service.LiveAPI
import com.example.risogelato.domain.entity.ClipInfoList
import com.example.risogelato.domain.entity.Live

interface LiveDataSource {

    fun start(): Live

    fun stop(clipInfoList: ClipInfoList)
}

class LiveDataSourceImpl(context: Context): LiveDataSource {

    private val liveAPI = RetrofitFactory(context).getService(LiveAPI::class.java)

    override fun start(): Live {
        val call = liveAPI.start()

        return RemoteExecutor.execute(call) {
            throw RuntimeException("라이브 정보를 불러오지 못했습니다.")
        } ?: throw RuntimeException("라이브 정보를 불러오지 못했습니다.")
    }

    override fun stop(clipInfoList: ClipInfoList) {
        val call = liveAPI.stop(OkHttpUtil.getJsonBody(clipInfoList))
        RemoteExecutor.execute(call) {
            throw RuntimeException("라이브 종료에 실패했습니다.")
        } ?: throw RuntimeException("라이브 종료에 실패했습니다.")
    }
}
