package com.kbratkovic.investmentgoldportfolio.repository

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.network.RetrofitInstance
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceResponse
import retrofit2.Response

class Repository(
    private val db: AppDatabase
) {

    // Database
    val getAllInvestmentItems: LiveData<List<InvestmentItem>> =
        db.investmentItemDao().getAllInvestmentItems()

    suspend fun addInvestmentItem(item: InvestmentItem) =
        db.investmentItemDao().addInvestmentItem(item)

    suspend fun deleteInvestmentItem(id: Int) {
        db.investmentItemDao().deleteInvestmentItem(id)
    }

    suspend fun undoDeleteInvestmentItem(id: Int) =
        db.investmentItemDao().undoDeleteInvestmentItem(id)


    // API
    suspend fun getMetalPriceFromApi(api_key: String, base: String, currencies: String): Response<MetalPriceResponse> {
        return RetrofitInstance.api.getMetalPriceFromApi(api_key, base, currencies)
    }


}