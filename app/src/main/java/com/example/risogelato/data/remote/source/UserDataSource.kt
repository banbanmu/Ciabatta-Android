package com.example.risogelato.data.remote.source

import android.content.Context
import com.example.risogelato.common.ACCESS_TOKEN
import com.example.risogelato.common.IS_LOGGED_IN
import com.example.risogelato.common.OkHttpUtil
import com.example.risogelato.data.local.SharedPreferencesManager
import com.example.risogelato.data.remote.RemoteExecutor
import com.example.risogelato.data.remote.RetrofitFactory
import com.example.risogelato.data.remote.service.UserAPI
import com.example.risogelato.domain.entity.User
import java.lang.RuntimeException

interface UserDataSource {

    fun signIn(username: String, password: String): User

    fun signUp(username: String, password: String, phone: String): User
}

class UserDataSourceImpl(context: Context) : UserDataSource {

    private val userAPI = RetrofitFactory(context).getService(UserAPI::class.java)
    private val sharedPreferences = SharedPreferencesManager(context)

    override fun signIn(username: String, password: String): User {
        val body = OkHttpUtil.getJsonBody(mapOf(
            "username" to username,
            "password" to password
        ))
        val call =  userAPI.signIn(body)
        val user = RemoteExecutor.execute(call) {
            throw RuntimeException("로그인에 실패했습니다")
        } ?: throw RuntimeException("로그인에 실패했습니다")

        sharedPreferences.put(ACCESS_TOKEN, user.token)
        sharedPreferences.put(IS_LOGGED_IN, true)

        return user
    }

    override fun signUp(username: String, password: String, phone: String): User {
        val body = OkHttpUtil.getJsonBody(mapOf(
            "username" to username,
            "password" to password,
            "phoneNumber" to phone
        ))
        val call =  userAPI.signUp(body)
        val user = RemoteExecutor.execute(call) {
            throw RuntimeException("회원가입에 실패했습니다")
        } ?: throw RuntimeException("회원가입에 실패했습니다")

        sharedPreferences.put(ACCESS_TOKEN, user.token)
        sharedPreferences.put(IS_LOGGED_IN, true)

        return user
    }
}
