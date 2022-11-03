package com.kbratkovic.investmentgoldportfolio.ui

import android.widget.Toast
import androidx.lifecycle.*
import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.repository.Repository
import com.kbratkovic.investmentgoldportfolio.util.Resource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.net.SocketTimeoutException

//class AddNewItemViewModel(application: Application) : AndroidViewModel(application) {
class MainViewModel(
    private val repository: Repository
) : ViewModel() {

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

    val currentGoldPrice: MutableLiveData<Resource<GoldPriceResponse>> = MutableLiveData()


    fun getCurrentGoldPrice(symbol: String, currency: String) = viewModelScope.launch {
        currentGoldPrice.postValue(Resource.Loading())

        try {
            val response = repository.getCurrentGoldPrice(symbol, currency)
            currentGoldPrice.postValue(handleCurrentGoldPriceResponse(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
        }

    }

    private fun handleCurrentGoldPriceResponse(response: Response<GoldPriceResponse>) : Resource<GoldPriceResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return  Resource.Error(response.message())
    }


//    fun addInvestmentItem(item: InvestmentItem) {
//        viewModelScope.launch {
//            repository.addInvestmentItem(item)
//        }
//    }

}