package com.example.risogelato.domain.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String
) : Parcelable

data class CategoryList(
    @SerializedName("categories") val categories: List<Category>
)
