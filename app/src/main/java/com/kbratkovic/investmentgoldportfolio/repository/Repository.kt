package com.kbratkovic.investmentgoldportfolio.repository

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.network.RetrofitInstance
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.network.response.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceApiComResponse
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.RATES_API_KEY
import retrofit2.Response

class Repository(
    private val db: AppDatabase
) {

    // database
    val getAllInvestmentItems: LiveData<List<InvestmentItem>> =
        db.investmentItemDao().getAllInvestmentItems()

    suspend fun addInvestmentItem(item: InvestmentItem) =
        db.investmentItemDao().addInvestmentItem(item)



    // api
//    suspend fun getCurrentGoldPrice(symbol: String, currency: String): Response<GoldPriceResponse> {
//        return RetrofitInstance.api.getCurrentGoldPrice(symbol, currency)
//    }

    suspend fun getCurrentGoldPriceFromMetalPriceApiCom(api_key: String, base: String, currencies: String): Response<MetalPriceApiComResponse> {
        return RetrofitInstance.api.getCurrentGoldPriceFromMetalPriceApiCom(api_key, base, currencies)
    }


//    suspend fun getCurrencyRatesBaseEUR() =
//        RetrofitInstance.api.getCurrencyRatesBaseEUR("EUR", RATES_API_KEY)
//
//
//    suspend fun getCurrencyRatesBaseUSD() =
//        RetrofitInstance.api.getCurrencyRatesBaseUSD("USD", RATES_API_KEY)

}