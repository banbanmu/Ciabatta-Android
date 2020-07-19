package com.example.risogelato.data.remote.service

import com.example.risogelato.domain.entity.Order
import com.example.risogelato.domain.entity.OrderList
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderAPI {

    @GET("/order")
    fun list(): Call<OrderList>

    @POST("/order/start")
    fun start(
        @Body params: RequestBody
    ): Call<Order>

    @POST("/order/finish")
    fun finish(
        @Body params: RequestBody
    ): Call<Order>
}
