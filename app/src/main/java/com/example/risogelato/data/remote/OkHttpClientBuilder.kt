package com.example.risogelato.data.remote

import android.content.Context
import com.example.risogelato.common.ACCESS_TOKEN
import com.example.risogelato.data.local.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkHttpClientBuilder(context: Context) {

    private val sharedPreferences = SharedPreferencesManager(context)

    private val builder = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addNetworkInterceptor(this::getTokenResponse)
        .addInterceptor(getLoggingInterceptor())

    private fun getTokenResponse(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        sharedPreferences.getString(ACCESS_TOKEN)?.let {
            builder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(builder.build())
    }

    private fun getLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun build() = builder.build()
}
