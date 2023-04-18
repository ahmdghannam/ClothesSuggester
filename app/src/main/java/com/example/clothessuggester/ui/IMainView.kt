package com.example.clothessuggester.ui

import com.example.clothessuggester.model.dto.NationalResponse

interface IMainView {
    fun showErrorMessage()
    fun updateUiState(response: NationalResponse)
}