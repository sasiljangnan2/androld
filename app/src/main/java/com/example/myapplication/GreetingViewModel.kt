package com.example.myapplication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GreetingViewModel : ViewModel() {
    val inputValue = mutableStateOf("")
    val displayText = mutableStateOf("Hello Android")
    
    fun updateText() {
        displayText.value = inputValue.value
    }
}

