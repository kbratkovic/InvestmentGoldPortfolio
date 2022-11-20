package com.kbratkovic.investmentgoldportfolio.repository

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.api.RetrofitInstance
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.RATES_API_KEY
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.RATES_BASE_URL

class Repository(
    private val db: AppDatabase
) {

    val getAllInvestmentItems: LiveData<List<InvestmentItem>> =
        db.investmentItemDao().getAllInvestmentItems()

    suspend fun addInvestmentItem(item: InvestmentItem) =
        db.investmentItemDao().addInvestmentItem(item)


    suspend fun getCurrentGoldPrice(symbol: String, currency: String) =
        RetrofitInstance.api.getCurrentGoldPrice(symbol, currency)


    suspend fun getCurrencyRates(baseCurrency: String) =
        RetrofitInstance.api.getCurrencyRates(baseCurrency, RATES_API_KEY)

}