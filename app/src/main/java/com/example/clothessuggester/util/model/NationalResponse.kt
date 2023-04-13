package com.example.clothessuggester.util.model

import com.google.gson.annotations.SerializedName

data class NationalResponse(
    val location:Location,
    @SerializedName("current")
    val weather:Weather
)