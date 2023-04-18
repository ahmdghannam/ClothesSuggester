package com.example.clothessuggester.presenters

import android.content.Context
import android.os.Build
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
    private val applicationContext: Context
) {

    init {
        PrefsUtil.initPreferences(applicationContext)
    }

    private val weatherApi by lazy {
        WeatherApiService(
            { view.updateUiState(it) },
            { view.showErrorMessage() }
        )
    }
    private val dataBase by lazy {
        ClothesDataBase()
    }


    private fun getLastWornClothes(): String {
        return PrefsUtil.clothesLink ?: ""
    }

    fun getWelcomingWords(isDay:Boolean):String{
      return  if (isDay) applicationContext.getString(R.string.good_morning)
      else applicationContext.getString(R.string.good_evening)
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
        return applicationContext.getString(R.string.location)
    }

    fun changeWeatherStatus() {
        val location = getCurrentLocation()
        weatherApi.getWeatherStatus(location)
    }


    private fun daysBetweenNowAndLastChecked(): DaysBetween {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) throw Exception("Api not compatible") // handle this
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


    fun getClothesPhoto(temperature: Int): String {
        val lastWornClothes = getLastWornClothes()
        return when (daysBetweenNowAndLastChecked()) {
            DaysBetween.CONSECUTIVE -> changeClothesWithRandomOne(temperature, lastWornClothes)
            DaysBetween.SAME_DAY -> lastWornClothes
            DaysBetween.ONE_OR_MORE -> changeClothesWithRandomOne(temperature)
        }
    }


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLastCheckedDate(LocalDate.now().toString())
        }
        return currentClothes
    }

    private fun getTemperatureEvaluation(temperature: Int): TempratureStatus {
        return when {
            // dont use hardcoded numbers
            temperature < LOW_TEMPERATURE -> TempratureStatus.COLD
            temperature in MID_TEMPERATURES -> TempratureStatus.NORMAL
            else -> TempratureStatus.HOT
        }
    }
     fun weatherStatus(temperature: Int): TempratureStatus {
        return when {
            temperature < LOW_TEMPERATURE -> TempratureStatus.COLD
            temperature in MID_TEMPERATURES -> TempratureStatus.NORMAL
            else -> TempratureStatus.HOT
        }
    }

    fun getWeatherLottieAnimation(temperature: Int): Int {
       return when (weatherStatus(temperature)) {
            TempratureStatus.COLD -> R.raw.weather_umbrella
            TempratureStatus.NORMAL -> R.raw.weather_cloudy
            TempratureStatus.HOT -> R.raw.weather_sunny
        }

    }

    companion object {
        private const val LOW_TEMPERATURE = 15
        private const val HIGH_TEMPERATURE = 25
        private val MID_TEMPERATURES = LOW_TEMPERATURE..HIGH_TEMPERATURE
    }

}