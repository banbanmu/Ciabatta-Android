package com.example.risogelato.common

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

object OkHttpUtil {

    private val TYPE_JSON = MediaType.parse("application/json; charset=utf-8")

    fun getJsonBody(target: Any): RequestBody {
        return RequestBody.create(TYPE_JSON, Gson().toJson(target))
    }
}
