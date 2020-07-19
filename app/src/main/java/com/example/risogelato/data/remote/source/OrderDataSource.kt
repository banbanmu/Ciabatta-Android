package com.example.risogelato.data.remote.source

import android.content.Context
import com.example.risogelato.common.OkHttpUtil
import com.example.risogelato.data.remote.RemoteExecutor
import com.example.risogelato.data.remote.RetrofitFactory
import com.example.risogelato.data.remote.service.OrderAPI
import com.example.risogelato.domain.entity.Order

interface OrderDataSource {

    fun list(): List<Order>

    fun start(orderId: String, storeId: Int)

    fun finish(orderId: String, storeId: Int)
}

class OrderDataSourceImpl(context: Context) : OrderDataSource {

    private val orderAPI = RetrofitFactory(context).getService(OrderAPI::class.java)

    override fun list(): List<Order> {
        val call = orderAPI.list()
        val orderList = RemoteExecutor.execute(call) {
            throw RuntimeException("주문 정보를 불러오지 못했습니다")
        }

        return orderList?.orders.orEmpty()
    }

    override fun start(orderId: String, storeId: Int) {
        val params = OkHttpUtil.getJsonBody(mapOf(
            "orderId" to orderId,
            "storeId" to storeId
        ))
        val call = orderAPI.start(params)

        RemoteExecutor.execute(call) {
            throw RuntimeException("조리를 시작하지 못했습니다")
        }
    }

    override fun finish(orderId: String, storeId: Int) {
        val params = OkHttpUtil.getJsonBody(mapOf(
            "orderId" to orderId,
            "storeId" to storeId
        ))
        val call = orderAPI.finish(params)

        RemoteExecutor.execute(call) {
            throw RuntimeException("조리를 종료하지 못했습니다")
        }
    }
}
