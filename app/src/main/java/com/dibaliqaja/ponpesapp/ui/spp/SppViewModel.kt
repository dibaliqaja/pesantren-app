package com.dibaliqaja.ponpesapp.ui.spp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SppViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is spp Fragment"
    }
    val text: LiveData<String> = _text
}