package com.example.risogelato.domain.entity

import com.google.gson.annotations.SerializedName

data class Live(
    @SerializedName("uid") val uid: Int,
    @SerializedName("channelName") val channelName: String
)
