package com.example.clothessuggester.datasource

import com.example.clothessuggester.BuildConfig
import com.example.clothessuggester.util.convertors.convertJsonToNationalResponseObject
import com.example.clothessuggester.util.model.NationalResponse
import okhttp3.*
import java.io.IOException

fun makeRequestUsingOKHTTP(
    location: String,
    uiChanging: (NationalResponse) -> Unit
) {
    val client = OkHttpClient.Builder().build()
    val httpUrl = createHttpUrl(location)
    val request = createRequest(httpUrl)
    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            throw Exception(e.message.toString())
        }

        override fun onResponse(call: Call, response: Response) {
            val jsonString = response.body?.string()
            val nationalResponse = convertJsonToNationalResponseObject(jsonString)
            nationalResponse?.let {
                uiChanging(it)
            }
        }
    })
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


