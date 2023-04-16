package com.example.clothessuggester.model.dto

import com.google.gson.annotations.SerializedName
data class Location(
    @SerializedName("name")
    val city:String,
    @SerializedName("country")
    val country:String,
    @SerializedName("localtime")
    val localTime:String,
)