package com.example.risogelato.data.remote.source

import android.content.Context
import com.example.risogelato.common.OkHttpUtil
import com.example.risogelato.data.remote.RemoteExecutor
import com.example.risogelato.data.remote.RetrofitFactory
import com.example.risogelato.data.remote.service.StoreAPI
import com.example.risogelato.domain.entity.Store
import com.example.risogelato.domain.entity.StoreRequest

interface StoreDataSource {

    fun register(store: StoreRequest)

    fun update(store: StoreRequest)

    fun get(): Store
}

class StoreDataSourceImpl(context: Context) : StoreDataSource {

    private val storeAPI = RetrofitFactory(context).getService(StoreAPI::class.java)

    override fun register(store: StoreRequest) {
        val body = OkHttpUtil.getJsonBody(store)
        val call = storeAPI.register(body)

        RemoteExecutor.execute(call) {
            throw RuntimeException("가게 등록에 실패했습니다")
        } ?: throw RuntimeException("가게 등록에 실패했습니다")
    }

    override fun update(store: StoreRequest) {
        val body = OkHttpUtil.getJsonBody(store)
        val call = storeAPI.update(body)

        RemoteExecutor.execute(call) {
            throw RuntimeException("가게 정보 수정에 실패했습니다")
        } ?: throw RuntimeException("가게 정보 수정에 실패했습니다")
    }

    override fun get(): Store {
        val call = storeAPI.get()

        return RemoteExecutor.execute(call) {
            throw RuntimeException("가게 정보를 불러오지 못했습니다")
        } ?: throw RuntimeException("가게 정보를 불러오지 못했습니다")
    }

}
