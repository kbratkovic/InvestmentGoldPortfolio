package com.kbratkovic.investmentgoldportfolio.repository

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.network.RetrofitInstance
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceApiComResponse
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
    suspend fun getCurrentGoldPriceFromMetalPriceApiCom(api_key: String, base: String, currencies: String): Response<MetalPriceApiComResponse> {
        return RetrofitInstance.api.getCurrentGoldPriceFromMetalPriceApiCom(api_key, base, currencies)
    }


}