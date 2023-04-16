package com.example.clothessuggester.util.preferences

import android.content.Context
import android.content.SharedPreferences

object PrefsUtil {

    private var sharedPreferences: SharedPreferences? = null
    private const val SHARED_PREFERENCES_NAME = "CLOTHES_LINK_SHARED_PREFERENCES"
    private const val KEY_CLOTHES = "CLOTHES_LINK_KEY"
    private const val KEY_DATE = "LAST_CHECKED_DATE_KEY"

    fun initPreferences(context: Context) {
        sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var clothesLink: String?
        get() = sharedPreferences?.getString(KEY_CLOTHES, null)
        set(value) {
            sharedPreferences?.edit()?.putString(KEY_CLOTHES, value)?.apply()
        }

    var lastCheckedDate: String?
        get() = sharedPreferences?.getString(KEY_DATE, null)
        set(value) {
            sharedPreferences?.edit()?.putString(KEY_DATE, value)?.apply()
        }

}