package com.kbratkovic.investmentgoldportfolio.ui.addNewItem

import android.app.Application
import androidx.lifecycle.*
import com.kbratkovic.investmentgoldportfolio.Repository
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.database.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.database.InvestmentItemDao
import kotlinx.coroutines.launch

//class AddNewItemViewModel(application: Application) : AndroidViewModel(application) {
class AddNewItemViewModel(repository: Repository) : ViewModel() {

    // OK RADI
//    init {
//        val investmentItemDao = AppDatabase.getDatabase(application).investmentItemDao()
//        val a  = 5
//        var b = a + 4
//    }

//    private val repository: Repository = Repository(AppDatabase.getDatabase().investmentDao())
//
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is AddNewItem Fragment"
//    }
//    val text: LiveData<String> = _text
//
    val allInvestmentItems: LiveData<List<InvestmentItem>> = repository.getAllInvestmentItems
//
//    fun addInvestmentItem(item: InvestmentItem) {
//        viewModelScope.launch {
//            repository.addInvestmentItem(item)
//        }
//    }

}