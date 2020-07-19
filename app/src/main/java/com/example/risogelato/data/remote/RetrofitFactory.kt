package com.example.risogelato.data.remote

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://ec2-13-209-89-24.ap-northeast-2.compute.amazonaws.com:8080"

class RetrofitFactory(context: Context) {

    private val retrofit = Retrofit.Builder()
        .client(OkHttpClientBuilder(context).build())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    fun <S> getService(serviceClass: Class<S>) = retrofit.create(serviceClass)
}
