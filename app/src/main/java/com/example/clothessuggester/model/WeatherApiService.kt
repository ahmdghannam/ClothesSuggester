package com.example.clothessuggester.model

import android.util.Log
import com.example.clothessuggester.BuildConfig
import com.example.clothessuggester.util.convertors.convertJsonToNationalResponseObject
import com.example.clothessuggester.model.dto.NationalResponse
import okhttp3.*
import java.io.IOException

class WeatherApiService(
    private val onSuccess: (NationalResponse) -> Unit,
    private val onFailure: () -> Unit
) : Callback {
    fun makeRequestUsingOKHTTP(location: String) {
        val client = OkHttpClient.Builder().build()
        val httpUrl = createHttpUrl(location)
        val request = createRequest(httpUrl)
        client.newCall(request).enqueue(this)
    }

    private fun createRequest(httpUrl: HttpUrl): Request {
        return Request
            .Builder()
            .url(httpUrl)
            .build()
    }

    private fun createHttpUrl(location: String): HttpUrl {
        return HttpUrl
            .Builder()
            .scheme("http")
            .host("api.weatherstack.com")
            .addPathSegment("/current")
            .addQueryParameter("access_key", BuildConfig.API_KEY)
            .addQueryParameter("query", location)
            .build()
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.i("onFailure", "onFailure: $e ")
        onFailure()
    }

    override fun onResponse(call: Call, response: Response) {
        val jsonString = response.body?.string()
        val nationalResponse = convertJsonToNationalResponseObject(jsonString)
        nationalResponse?.let { onSuccess(it) }
    }

}
