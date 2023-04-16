package com.example.clothessuggester.model.dto

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("observation_time")
    val lastCheckTime: String,
    @SerializedName("temperature")
    val temperature: Int,
    @SerializedName("weather_icons")
    val weatherStatusIcon: List<String>,
    @SerializedName("weather_descriptions")
    val weatherDescription: List<String>,
    @SerializedName("wind_speed")
    val windSpeed: Int,
    @SerializedName("wind_dir")
    val windDirection: String,
    @SerializedName("visibility")
    val visibilityDegree: Int,
    @SerializedName("is_day")
    val isDay: Boolean,
)
