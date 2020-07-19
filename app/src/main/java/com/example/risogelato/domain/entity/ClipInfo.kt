package com.example.risogelato.domain.entity

import com.google.gson.annotations.SerializedName

data class ClipInfo(
    @SerializedName("name") val menuName: String,
    @SerializedName("startMilli") val startMillis: Long,
    @SerializedName("durationMilli") val durationMillis: Long
)

data class ClipInfoList(
    @SerializedName("clipInfos") val clipInfos: List<ClipInfo>
)
