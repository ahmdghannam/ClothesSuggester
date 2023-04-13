package com.example.clothessuggester.util.model

import com.google.gson.annotations.SerializedName
data class Location(
    @SerializedName("name")
    val city:String,
    @SerializedName("country")
    val country:String,
    @SerializedName("localtime")
    val localTime:String,
)