package com.kbratkovic.investmentgoldportfolio.api

import com.kbratkovic.investmentgoldportfolio.models.CurrencyRatesResponse
import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_PRICE_API_KEY
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.RATES_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @Headers("x-access-token: $GOLD_PRICE_API_KEY")
    @GET("{symbol}/{currency}")
    suspend fun getCurrentGoldPrice(
        @Path("symbol") symbol: String = "XAU",
        @Path("currency") currency: String = "EUR"
    ): Response<GoldPriceResponse>


    @GET("https://anyapi.io/api/v1/exchange/rates/")
    suspend fun getCurrencyRates(
        @Query("base") baseCurrency: String,
        @Query("apiKey") apiKey: String
    ): Response<CurrencyRatesResponse>


}