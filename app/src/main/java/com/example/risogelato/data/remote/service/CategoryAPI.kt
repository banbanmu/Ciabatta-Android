package com.example.risogelato.data.remote.service

import com.example.risogelato.domain.entity.CategoryList
import retrofit2.Call
import retrofit2.http.GET

interface CategoryAPI {

    @GET("/store/category")
    fun list(): Call<CategoryList>
}
