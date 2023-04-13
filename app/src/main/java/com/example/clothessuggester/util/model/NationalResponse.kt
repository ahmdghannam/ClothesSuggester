package com.example.clothessuggester.util.model

import com.google.gson.annotations.SerializedName

data class NationalResponse(
    @SerializedName("location")
    val location:Location,
    @SerializedName("current")
    val weather:Weather
)