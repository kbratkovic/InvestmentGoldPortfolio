package com.kbratkovic.investmentgoldportfolio.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.repository.Repository
import com.kbratkovic.investmentgoldportfolio.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.net.SocketTimeoutException


class MainViewModel(
    private val repository: Repository,
    private val context: Application
) : AndroidViewModel(context) {

    private var mOnDataChangeListener: OnDataChangeListener? = null

    fun setOnDataChangeListener(onDataChangeListener: OnDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener
    }

    interface OnDataChangeListener {
        fun onDataChanged(message: String?)
    }


    val allInvestmentItems: LiveData<List<InvestmentItem>> = repository.getAllInvestmentItems

    val currentGoldPrice: MutableLiveData<Resource<GoldPriceResponse>> = MutableLiveData()


    fun getCurrentGoldPrice(symbol: String, currency: String) = viewModelScope.launch {
        currentGoldPrice.postValue(Resource.Loading())

        try {
            val response = repository.getCurrentGoldPrice(symbol, currency)
            currentGoldPrice.postValue(handleCurrentGoldPriceResponse(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
            mOnDataChangeListener?.onDataChanged( context.getString(R.string.network_error))
        }

    }

    private fun handleCurrentGoldPriceResponse(response: Response<GoldPriceResponse>) : Resource<GoldPriceResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    fun addInvestmentItem(item: InvestmentItem) =  viewModelScope.launch {
        try {
            repository.addInvestmentItem(item)
            Toast.makeText(context, "Item saved successfully",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Save error!",Toast.LENGTH_SHORT).show()
        }

    }


}