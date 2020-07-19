package com.example.risogelato.domain.entity

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String
)

data class CategoryList(
    @SerializedName("categories") val categories: List<Category>
)
