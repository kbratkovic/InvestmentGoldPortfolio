package com.kbratkovic.investmentgoldportfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kbratkovic.investmentgoldportfolio.repository.Repository
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel

class ViewModelProviderFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}