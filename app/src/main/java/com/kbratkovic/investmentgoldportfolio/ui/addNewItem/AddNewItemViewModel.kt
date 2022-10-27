package com.kbratkovic.investmentgoldportfolio.ui.addNewItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddNewItemViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is AddNewItem Fragment"
    }
    val text: LiveData<String> = _text
}