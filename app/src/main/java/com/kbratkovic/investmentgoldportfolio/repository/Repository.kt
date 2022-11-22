package com.kbratkovic.investmentgoldportfolio.repository

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.network.RetrofitInstance
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.RATES_API_KEY

class Repository(
    private val db: AppDatabase
) {

    val getAllInvestmentItems: LiveData<List<InvestmentItem>> =
        db.investmentItemDao().getAllInvestmentItems()

    suspend fun addInvestmentItem(item: InvestmentItem) =
        db.investmentItemDao().addInvestmentItem(item)


    suspend fun getCurrentGoldPrice(symbol: String, currency: String) =
        RetrofitInstance.api.getCurrentGoldPrice(symbol, currency)


    suspend fun getCurrencyRatesBaseEUR() =
        RetrofitInstance.api.getCurrencyRatesBaseEUR("EUR", RATES_API_KEY)


    suspend fun getCurrencyRatesBaseUSD() =
        RetrofitInstance.api.getCurrencyRatesBaseUSD("USD", RATES_API_KEY)

}