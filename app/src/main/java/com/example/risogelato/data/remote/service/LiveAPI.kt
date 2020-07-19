package com.example.risogelato.data.remote.service

import com.example.risogelato.domain.entity.Live
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface LiveAPI {

    @GET("/live/start")
    fun start(): Call<Live>

    @POST("/live/stop")
    fun stop(): Call<ResponseBody>
}
