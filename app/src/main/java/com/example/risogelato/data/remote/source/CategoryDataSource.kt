package com.example.risogelato.data.remote.source

import android.content.Context
import com.example.risogelato.data.remote.RemoteExecutor
import com.example.risogelato.data.remote.RetrofitFactory
import com.example.risogelato.data.remote.service.CategoryAPI
import com.example.risogelato.domain.entity.Category

interface CategoryDataSource {

    fun list(): List<Category>
}

class CategoryDataSourceImpl(context: Context) : CategoryDataSource {

    private val categoryAPI = RetrofitFactory(context).getService(CategoryAPI::class.java)

    override fun list(): List<Category> {
        val call =  categoryAPI.list()
        val categoryList = RemoteExecutor.execute(call) {
            throw RuntimeException("카테고리를 불러오지 못했습니다")
        }

        return categoryList?.categories.orEmpty()
    }
}
