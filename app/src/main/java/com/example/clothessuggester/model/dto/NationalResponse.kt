package com.example.clothessuggester.model.dto

import com.google.gson.annotations.SerializedName

data class NationalResponse(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val weather: Weather
)