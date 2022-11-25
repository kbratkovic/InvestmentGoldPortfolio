package com.kbratkovic.investmentgoldportfolio.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.mappers.CurrencyExchangeRatesMapper
import com.kbratkovic.investmentgoldportfolio.domain.mappers.GoldPriceMapper
import com.kbratkovic.investmentgoldportfolio.domain.mappers.MetalPriceApiComMapper
import com.kbratkovic.investmentgoldportfolio.domain.models.CurrencyRates
import com.kbratkovic.investmentgoldportfolio.domain.models.GoldPrice
import com.kbratkovic.investmentgoldportfolio.network.response.CurrencyExchangeRatesResponse
import com.kbratkovic.investmentgoldportfolio.network.response.GoldPriceResponse
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

    private val _currentGoldPrice: MutableLiveData<Resource<GoldPrice>> = MutableLiveData()
    val currentGoldPrice: LiveData<Resource<GoldPrice>> = _currentGoldPrice

    private val _currentGoldPriceFromMetalPriceApiCom: MutableLiveData<Resource<MetalPriceApiCom>> = MutableLiveData()
    val currentGoldPriceFromMetalPriceApiCom: LiveData<Resource<MetalPriceApiCom>> = _currentGoldPriceFromMetalPriceApiCom

    private val _currencyRatesBaseEUR: MutableLiveData<Resource<CurrencyRates>> = MutableLiveData()
    val currencyRatesBaseEUR: LiveData<Resource<CurrencyRates>> = _currencyRatesBaseEUR

    private val _currencyRatesBaseUSD: MutableLiveData<Resource<CurrencyRates>> = MutableLiveData()
    val currencyRatesBaseUSD: LiveData<Resource<CurrencyRates>> = _currencyRatesBaseUSD


    // Current Gold Price
    fun getCurrentGoldPrice(symbol: String, currency: String) = viewModelScope.launch {
        _currentGoldPrice.postValue(Resource.Loading())

        try {
            val response = repository.getCurrentGoldPrice(symbol, currency)
            _currentGoldPrice.postValue(handleCurrentGoldPriceResponse(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
            mOnDataChangeListener?.onDataChanged(context.getString(R.string.network_error))
        }

    }

    private fun handleCurrentGoldPriceResponse(response: Response<GoldPriceResponse>) : Resource<GoldPrice> {
        if (response.isSuccessful) {
            response.body()?.let { goldPrice ->
                return Resource.Success(GoldPriceMapper.buildFrom(goldPrice))
            }
        }
        return Resource.Error(response.message())
    }


    // Current Gold Price From MetalPriceApi.com
//    fun getCurrentGoldPriceFromMetalPriceApiCom() = viewModelScope.launch {
//        _currentGoldPriceFromMetalPriceApiCom.postValue(Resource.Loading())
//
//        try {
//            val response = repository.getCurrentGoldPriceFromMetalPriceApiCom(Constants.METAL_PRICE_API_COM_KEY, Constants.CURRENCY_USD_CODE, "${Constants.CURRENCY_EUR_CODE},${Constants.GOLD_CODE}")
//            _currentGoldPriceFromMetalPriceApiCom.postValue(handleCurrentGoldPriceFromMetalPriceApiCo(response))
//        } catch (e: SocketTimeoutException) {
//            Timber.e(e.localizedMessage)
//            mOnDataChangeListener?.onDataChanged(context.getString(R.string.network_error))
//        }
//
//    }
//
//    private fun handleCurrentGoldPriceFromMetalPriceApiCo(response: Response<MetalPriceApiComResponse>) : Resource<MetalPriceApiCom> {
//        if (response.isSuccessful) {
//            response.body()?.let { goldPrice ->
//                return Resource.Success(MetalPriceApiComMapper.buildFrom(goldPrice))
//            }
//        }
//        return Resource.Error(response.message())
//    }


    // Currency Rates base EUR
    fun getCurrencyRatesBaseEUR() = viewModelScope.launch {

        try {
            val response = repository.getCurrencyRatesBaseEUR()
            _currencyRatesBaseEUR.postValue(handleCurrencyRatesBaseEurResponse(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
            mOnDataChangeListener?.onDataChanged( context.getString(R.string.network_error))
        }

    }

    private fun handleCurrencyRatesBaseEurResponse(response: Response<CurrencyExchangeRatesResponse>) : Resource<CurrencyRates> {
        if (response.isSuccessful) {
            response.body()?.let { currencyRatesResponse ->
                return Resource.Success(CurrencyExchangeRatesMapper.buildFrom(currencyRatesResponse))
            }
        }
        return Resource.Error(response.message())
    }


    // Currency Rates base USD
    fun getCurrencyRatesBaseUSD() = viewModelScope.launch {

        try {
            val response = repository.getCurrencyRatesBaseUSD()
            _currencyRatesBaseUSD.postValue(handleCurrencyRatesBaseUsdResponse(response))
        } catch (e: SocketTimeoutException) {
            Timber.e(e.localizedMessage)
            mOnDataChangeListener?.onDataChanged( context.getString(R.string.network_error))
        }

    }

    private fun handleCurrencyRatesBaseUsdResponse(response: Response<CurrencyExchangeRatesResponse>) : Resource<CurrencyRates> {
        if (response.isSuccessful) {
            response.body()?.let { currencyRatesResponse ->
                return Resource.Success(CurrencyExchangeRatesMapper.buildFrom(currencyRatesResponse))
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


}