package com.kbratkovic.investmentgoldportfolio.api

import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
//
    @Headers("x-access-token: $API_KEY")
    @GET("{symbol}/{currency}")
    suspend fun getCurrentGoldPrice(
        @Query("symbol") symbol: String = "XAU",
        @Query("currency") currency: String = "EUR"
    ): Response<GoldPriceResponse>

}