package com.example.clothessuggester.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clothessuggester.databinding.ActivityMainBinding

import com.example.clothessuggester.datasource.makeRequestUsingOKHTTP
import com.example.clothessuggester.util.model.NationalResponse

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        makeRequestUsingOKHTTP("32.46107762600448, 35.29948490785005",::changeUIStatus)
    }

    private fun changeUIStatus(response:NationalResponse){
        runOnUiThread {

        }
    }
}