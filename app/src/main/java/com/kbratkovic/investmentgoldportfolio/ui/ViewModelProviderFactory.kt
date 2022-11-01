package com.kbratkovic.investmentgoldportfolio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kbratkovic.investmentgoldportfolio.Repository
import com.kbratkovic.investmentgoldportfolio.ui.addNewItem.AddNewItemViewModel

class ViewModelProviderFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddNewItemViewModel(repository) as T
    }
}