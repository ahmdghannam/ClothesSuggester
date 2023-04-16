package com.example.clothessuggester.ui

import com.example.clothessuggester.model.dto.NationalResponse

interface IMainView {
    fun onApiFailure()
    fun onApiSuccess(response: NationalResponse)
}