package com.example.clothessuggester.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.clothessuggester.R
import com.example.clothessuggester.databinding.ActivityMainBinding

import com.example.clothessuggester.datasource.makeRequestUsingOKHTTP
import com.example.clothessuggester.util.model.NationalResponse
import com.example.clothessuggester.util.model.WeatherStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//       requestDataAccordingToLocation()
    }

    private fun requestDataAccordingToLocation() {
        val currentLocation = "32.32607774961586, 35.219691881093866"
        Log.i("MAIN_ACTIVITY", "requestDataAccordingToLocation: $currentLocation ")
        if (currentLocation == null) {
            showErrorMessage()
        } else {
            makeRequestUsingOKHTTP(currentLocation, ::changeUIStatus)
        }
    }

    private fun showErrorMessage() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Error")
            setMessage("can't detect location, please recheck the internet and permissions")
            setPositiveButton("ok") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(true)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun getUserLocation(): String? {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            Log.i("MAIN_ACTIVITY", "getUserLocation: $location ")
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                return "$latitude, $longitude"
            }

        }

        return null
    }

    private fun changeUIStatus(response: NationalResponse) {
//        Log.i("internetdata", "onResponse: ${response.toString()} ")
        runOnUiThread {
            updateWelcomingWords(response.weather.isDay)
            updateWeatherStatus(response)
            updateLottieImageAccordingToWeather(response.weather.temperature)
            updateClothesImageView(response.weather.temperature)
            updateSunglassesUmbrellaSuggestion(response.weather.temperature)
        }
    }

    private fun updateSunglassesUmbrellaSuggestion(temperature: Int) {
        val weatherStatus = weatherStatus(temperature)

        when (weatherStatus) {
            WeatherStatus.COLD -> suggestUmbrella()
            WeatherStatus.NORMAL -> hideSuggestionText()
            WeatherStatus.HOT -> suggestSunGlasses()
        }

    }

    private fun suggestSunGlasses() {
        binding.textviewSunGlassesOrUmbrella.apply {
            visibility = View.VISIBLE
            text = getString(R.string.sun_glasses_suggestion)
            val drawable = resources.getDrawable(R.drawable.ic_glasses, null)
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }

    private fun hideSuggestionText() {
        binding.textviewSunGlassesOrUmbrella.visibility = View.GONE
    }

    private fun suggestUmbrella() {
        binding.textviewSunGlassesOrUmbrella.apply {
            visibility = View.VISIBLE
            text = getString(R.string.take_umbrella)
            val drawable = resources.getDrawable(R.drawable.ic_umbrella, null)
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }

    private fun updateClothesImageView(temperature: Int) {
        val weatherStatus = weatherStatus(temperature)
        val photo = when (weatherStatus) {
            WeatherStatus.COLD -> getRandomColdClothes()
            WeatherStatus.NORMAL -> getRandomNormalClothes()
            WeatherStatus.HOT -> getRandomHotClothes()
        }
        loadImage(photo)
    }

    private fun loadImage(photo: String) {
        Glide.with(this)
            .load(photo)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imageviewClothes)
    }

    private fun getRandomHotClothes(): String {
        return "https://fabrilife.com/image-gallery/638741f4b738e-square.jpg"
    }

    private fun getRandomNormalClothes(): String {
        return "https://cdn.shopify.com/s/files/1/0896/8970/products/redprint_2_2f59a503-3ef7-4991-a247-3d40e9e58e63.jpg?v=1668774891"
    }

    private fun getRandomColdClothes(): String {
        return "https://www.insidehook.com/wp-content/uploads/2023/01/Ibex-Wool-Aire-Hoodie.jpg?fit=1200%2C800"
    }

    private fun updateLottieImageAccordingToWeather(temperature: Int) {
        val weatherStatus = weatherStatus(temperature)
        when (weatherStatus) {
            WeatherStatus.COLD -> changeLottieAnimation(R.raw.weather_umbrella)
            WeatherStatus.NORMAL -> changeLottieAnimation(R.raw.weather_cloudy)
            WeatherStatus.HOT -> changeLottieAnimation(R.raw.weather_sunny)
        }
        binding.lottieWeatherIcon.apply {
            playAnimation()
            loop(true)
        }
    }

    private fun changeLottieAnimation(animationId: Int) {
        binding.lottieWeatherIcon.setAnimation(animationId)
    }

    private fun weatherStatus(temperature: Int): WeatherStatus {
        return when {
            temperature < 15 -> WeatherStatus.COLD
            temperature in 15..20 -> WeatherStatus.NORMAL
            else -> WeatherStatus.HOT
        }
    }

    private fun updateWeatherStatus(response: NationalResponse) {
        binding.apply {
            val cityCountry = "${response.location.city} ${response.location.country} "
            textviewCityCountry.text = cityCountry
            textviewTime.text = response.location.localTime
            textviewWeatherStatus.text = response.weather.weatherDescription[0]
            val temp = response.weather.temperature.toString() + " c"
            textviewTemperature.text = temp
            val windSpeed = response.weather.windSpeed.toString() + " km/h"
            textviewWind.text = windSpeed
        }
        Toast.makeText(this, "status updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateWelcomingWords(isDay: Boolean) {
        val welcomingMessage = if (isDay) "Good morning" else "Good evening"
        binding.textviewWelcomingMessage.text = welcomingMessage
    }


}