package com.kbratkovic.investmentgoldportfolio.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.mappers.MetalPriceApiComMapper
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPriceApiCom
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceApiComResponse
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


//    private val _allInvestmentItems: LiveData<List<InvestmentItem>> = repository.getAllInvestmentItems
    val allInvestmentItems: LiveData<List<InvestmentItem>> = repository.getAllInvestmentItems

    private val _currentGoldPriceFromMetalPriceApiCom: MutableLiveData<Resource<MetalPriceApiCom>> = MutableLiveData()
    val currentGoldPriceFromMetalPriceApiCom: LiveData<Resource<MetalPriceApiCom>> = _currentGoldPriceFromMetalPriceApiCom


    // Current Gold Price From MetalPriceApi.com
    fun getCurrentGoldPriceFromMetalPriceApiCom() = viewModelScope.launch {
        _currentGoldPriceFromMetalPriceApiCom.postValue(Resource.Loading())

        try {
            val response = repository.getCurrentGoldPriceFromMetalPriceApiCom(Constants.METAL_PRICE_API_COM_KEY, Constants.CURRENCY_USD_CODE,
                "${Constants.CURRENCY_EUR_CODE},${Constants.GOLD_CODE},${Constants.SILVER_CODE},${Constants.PLATINUM_CODE},${Constants.PALLADIUM_CODE}")
            _currentGoldPriceFromMetalPriceApiCom.postValue(handleCurrentGoldPriceFromMetalPriceApiCo(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
            mOnDataChangeListener?.onDataChanged(context.getString(R.string.network_error))
        }
    }

    private fun handleCurrentGoldPriceFromMetalPriceApiCo(response: Response<MetalPriceApiComResponse>) : Resource<MetalPriceApiCom> {
        if (response.isSuccessful) {
            response.body()?.let { goldPrice ->
                if (goldPrice.success)
                    return Resource.Success(MetalPriceApiComMapper.buildFrom(goldPrice))
                else
                    return Resource.Error(response.message())
            }
        }
        return Resource.Error(response.message())
    }


    // Add New Gold Item To DB
    fun addInvestmentItem(item: InvestmentItem) =  viewModelScope.launch {
        try {
            repository.addInvestmentItem(item)
            Toast.makeText(context, "Item saved successfully",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Save error!",Toast.LENGTH_SHORT).show()
        }
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