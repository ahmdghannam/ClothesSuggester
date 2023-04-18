package com.example.clothessuggester.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.clothessuggester.R
import com.example.clothessuggester.databinding.ActivityMainBinding
import com.example.clothessuggester.model.dto.NationalResponse
import com.example.clothessuggester.model.dto.TempratureStatus
import com.example.clothessuggester.presenters.MainPresenter


class MainActivity : AppCompatActivity(), IMainView {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private val presenter by lazy {
        MainPresenter(this, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        addCallBacks()
        requestDataAccordingToLocation()
    }


    private fun addCallBacks() {
        binding.homeRefresh.setOnRefreshListener {
            requestDataAccordingToLocation()
            binding.root.isRefreshing = false
        }
    }

    private fun requestDataAccordingToLocation() {
        presenter.changeWeatherStatus()
    }

    override fun updateUiState(response: NationalResponse) {
        runOnUiThread {
            updateWelcomingWords(response.weather.isDay)
            updateWeatherStatus(response)
            updateLottieImageAccordingToWeather(response.weather.temperature)
            updateClothesImageView(response.weather.temperature)
            updateSunglassesUmbrellaSuggestion(response.weather.temperature)
        }
    }

    override fun showErrorToUser() {
        runOnUiThread {
            showErrorAlertDialog()
            setErrorLottieAnimation()
        }
    }

    private fun setErrorLottieAnimation() {
        val animationId = R.raw.lottie_error
        changeLottieAnimation(animationId)
        loopLottieAnimation()
    }

    private fun showErrorAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Error")
            setMessage("can't detect location, please recheck the internet and permissions")
            setPositiveButton("ok") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateSunglassesUmbrellaSuggestion(temperature: Int) {

        val weatherStatus = presenter.weatherStatus(temperature)

        when (weatherStatus) {
            TempratureStatus.COLD -> suggestUmbrella()
            TempratureStatus.NORMAL -> hideSuggestionText()
            TempratureStatus.HOT -> suggestSunGlasses()
        }

    }

    private fun suggestUmbrella() {
        binding.textviewSunGlassesOrUmbrella.apply {
            visibility = View.VISIBLE
            text = getString(R.string.take_umbrella)
            val drawable = resources.getDrawable(R.drawable.ic_umbrella, null)
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
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

    private fun updateClothesImageView(temperature: Int) {
        val photo = presenter.getClothesPhoto(temperature)
        loadClothesImage(photo)
    }

    private fun loadClothesImage(photo: String) {
        Log.i(TAG, "loadClothesImage: $photo")
        Glide.with(this)
            .load(photo)
            .error(R.drawable.ic_no_image)
            .into(binding.imageviewClothes)
    }

    private fun updateLottieImageAccordingToWeather(temperature: Int) {
        val animationId = presenter.getWeatherLottieAnimation(temperature)
        changeLottieAnimation(animationId)
        loopLottieAnimation()
    }

    private fun loopLottieAnimation() {
        binding.lottieWeatherIcon.apply {
            playAnimation()
            loop(true)
        }
    }

    private fun changeLottieAnimation(animationId: Int) {
        binding.lottieWeatherIcon.setAnimation(animationId)
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
        val welcomingMessage = presenter.getWelcomingWords(isDay)
        binding.textviewWelcomingMessage.text = welcomingMessage
    }


    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

}