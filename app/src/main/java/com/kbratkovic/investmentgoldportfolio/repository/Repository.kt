package com.kbratkovic.investmentgoldportfolio.repository

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.api.RetrofitInstance
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import retrofit2.Response

class Repository(
    private val db: AppDatabase
) {

    val getAllInvestmentItems: LiveData<List<InvestmentItem>> =
        db.investmentItemDao().getAllInvestmentItems()

    suspend fun addInvestmentItem(item: InvestmentItem) =
        db.investmentItemDao().addInvestmentItem(item)


    suspend fun getCurrentGoldPrice(symbol: String, currency: String) =
        RetrofitInstance.api.getCurrentGoldPrice(symbol, currency)

}