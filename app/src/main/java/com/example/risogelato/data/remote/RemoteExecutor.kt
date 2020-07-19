package com.example.risogelato.data.remote

import retrofit2.Call

object RemoteExecutor {

    fun <T> execute(call: Call<T>, onError: () -> Unit): T? {
        val httpResponse = call.execute()

        if (!httpResponse.isSuccessful) {
            onError()
            return null
        }

        return httpResponse.body()
    }
}
