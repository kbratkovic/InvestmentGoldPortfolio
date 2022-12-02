package com.kbratkovic.investmentgoldportfolio.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.mappers.MetalPriceMapper
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPrice
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceResponse
import com.kbratkovic.investmentgoldportfolio.repository.Repository
import com.kbratkovic.investmentgoldportfolio.util.Constants
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

    private val _metalPrice: MutableLiveData<Resource<MetalPrice>> = MutableLiveData()
    val metalPrice: LiveData<Resource<MetalPrice>> = _metalPrice


    // API
    fun getMetalPriceFromApi() = viewModelScope.launch {
        _metalPrice.postValue(Resource.Loading())

        try {
            val response = repository.getMetalPriceFromApi(Constants.API_KEY, Constants.CURRENCY_USD_CODE,
                "${Constants.CURRENCY_EUR_CODE},${Constants.GOLD_CODE},${Constants.SILVER_CODE},${Constants.PLATINUM_CODE},${Constants.PALLADIUM_CODE}")
            _metalPrice.postValue(handleMetalPriceFromApi(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
            mOnDataChangeListener?.onDataChanged(context.getString(R.string.network_error))
        }
    }

    private fun handleMetalPriceFromApi(response: Response<MetalPriceResponse>) : Resource<MetalPrice> {
        if (response.isSuccessful) {
            response.body()?.let { metalPriceResponse ->
                if (metalPriceResponse.success)
                    return Resource.Success(MetalPriceMapper.buildFrom(metalPriceResponse))
                else
                    return Resource.Error(context.getString(R.string.network_error))
            }
        }
        return Resource.Error(response.message())
    }


    // Database
    fun addInvestmentItem(item: InvestmentItem) =
        viewModelScope.launch {
            repository.addInvestmentItem(item)
            Toast.makeText(context, context.getString(R.string.message_save_successfull),Toast.LENGTH_SHORT).show()
    }


    fun deleteInvestmentItem(item: InvestmentItem) =
        viewModelScope.launch {
            repository.deleteInvestmentItem(item.id)
        }


    fun undoDeleteInvestmentItem(item: InvestmentItem) =
        viewModelScope.launch {
            repository.undoDeleteInvestmentItem(item.id)
        }

}