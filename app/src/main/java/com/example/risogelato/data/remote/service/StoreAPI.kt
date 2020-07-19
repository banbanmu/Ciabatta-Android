package com.example.risogelato.data.remote.service

import com.example.risogelato.domain.entity.Store
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface StoreAPI {

    @POST("/store")
    fun register(
        @Body params: RequestBody
    ): Call<ResponseBody>

    @PUT("/store")
    fun update(
        @Body params: RequestBody
    ): Call<ResponseBody>

    @GET("/store")
    fun get(): Call<Store>
}
