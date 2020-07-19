package com.example.risogelato.domain.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("phoneNumber") val phone: String,
    @SerializedName("category") val category: Category,
    @SerializedName("menu") val menu: List<Menu>
) : Parcelable

@Parcelize
data class Menu(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("description") val description: String,
    @SerializedName("lastCookVideoUrl") val lastCookVideoUrl: String?
) : Parcelable

data class StoreRequest(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("phoneNumber") val phone: String,
    @SerializedName("category") val category: String,
    @SerializedName("menu") val menu: List<Menu>
)
