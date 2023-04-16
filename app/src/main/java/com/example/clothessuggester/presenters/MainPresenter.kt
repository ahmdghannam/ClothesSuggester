package com.example.clothessuggester.presenters

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clothessuggester.R
import com.example.clothessuggester.model.ClothesDataBase
import com.example.clothessuggester.model.WeatherApiService
import com.example.clothessuggester.model.dto.DaysBetween
import com.example.clothessuggester.model.dto.TempratureStatus
import com.example.clothessuggester.ui.IMainView
import com.example.clothessuggester.util.preferences.PrefsUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class MainPresenter(
    private val view: IMainView,
    private val applcationContext: Context
) {

    init {
        PrefsUtil.initPreferences(applcationContext)
    }
    private val weatherApi by lazy {
        WeatherApiService(
            { view.onApiSuccess(it) },
            { view.onApiFailure() }
        )
    }
    private val dataBase by lazy {
        ClothesDataBase()
    }


    private fun getLastWornClothes(): String {
        return PrefsUtil.clothesLink ?: ""
    }

    private fun setLastWornClothes(currentClothes: String) {
        PrefsUtil.clothesLink = currentClothes
    }

    private fun getLastCheckedDate(): String {
        return PrefsUtil.lastCheckedDate ?: "2001-04-16"
    }

    private fun setLastCheckedDate(date: String) {
        PrefsUtil.lastCheckedDate = date
    }

    private fun getCurrentLocation(): String {
        return applcationContext.getString(R.string.location)
    }

    fun changeWeatherStatus() {
        val location = getCurrentLocation()
        weatherApi.makeRequestUsingOKHTTP(location)
    }


    private fun daysBetweenNowAndLastChecked(): DaysBetween {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) throw Exception("Api not compatible")
        val dateFromSharedPreferences = getLastCheckedDate()
        val currentDate = LocalDate.now().toString()

        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val date1 = LocalDate.parse(dateFromSharedPreferences, dateFormatter)
        val date2 = LocalDate.parse(currentDate, dateFormatter)

        val daysBetween = ChronoUnit.DAYS.between(date1, date2).absoluteValue

        return when (daysBetween) {
            0L -> DaysBetween.SAME_DAY
            1L -> DaysBetween.CONSECUTIVE
            else -> DaysBetween.ONE_OR_MORE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getClothesPhoto(temperature: Int): String {
        val lastWornClothes = getLastWornClothes()
        return when (daysBetweenNowAndLastChecked()) {
            DaysBetween.CONSECUTIVE -> changeClothesWithRandomOne(temperature, lastWornClothes)
            DaysBetween.SAME_DAY -> lastWornClothes
            DaysBetween.ONE_OR_MORE -> changeClothesWithRandomOne(temperature)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeClothesWithRandomOne(
        temperature: Int,
        except: String = ""
    ): String {
        val currentClothes = when (getTemperatureEvaluation(temperature)) {
            TempratureStatus.COLD -> dataBase.getColdClothes(except)
            TempratureStatus.NORMAL -> dataBase.getNormalClothes(except)
            TempratureStatus.HOT -> dataBase.getHotClothes(except)
        }
        setLastWornClothes(currentClothes)
        setLastCheckedDate(LocalDate.now().toString())
        return currentClothes
    }

    private fun getTemperatureEvaluation(temperature: Int): TempratureStatus {
        return when {
            temperature < 15 -> TempratureStatus.COLD
            temperature in 15..25 -> TempratureStatus.NORMAL
            else -> TempratureStatus.HOT
        }
    }

}