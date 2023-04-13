package com.example.clothessuggester.util.convertors

import android.util.Log
import com.example.clothessuggester.util.model.NationalResponse
import com.google.gson.Gson

fun convertJsonToNationalResponseObject(jsonString:String?):NationalResponse?{
    Log.i("batata", "jsonstring: $jsonString")
    return try{
        Gson().fromJson(jsonString,NationalResponse::class.java)
    }catch (ex:java.lang.Exception){
        Log.i("batata", "convertJson: ${ex.message.toString()} ")
        null
    }
}