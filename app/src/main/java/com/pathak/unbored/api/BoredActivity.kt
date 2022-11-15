package com.pathak.unbored.api

import com.google.gson.annotations.SerializedName

data class BoredActivity(
    @SerializedName("activity")
    val activity: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("participants")
    val participants: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("link")
    val link: String,
    @SerializedName("key")
    val key: Int,
    @SerializedName("accessibility")
    val accessibility: Double
)
