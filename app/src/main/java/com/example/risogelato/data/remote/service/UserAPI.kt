package com.example.risogelato.data.remote.service

import com.example.risogelato.domain.entity.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("/auth/signIn")
    fun signIn(
        @Body params: RequestBody
    ): Call<User>

    @POST("/auth/signUp")
    fun signUp(
        @Body params: RequestBody
    ): Call<User>
}
