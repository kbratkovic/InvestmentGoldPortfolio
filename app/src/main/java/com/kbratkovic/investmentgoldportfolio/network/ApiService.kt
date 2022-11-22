package com.kbratkovic.investmentgoldportfolio.network

import com.kbratkovic.investmentgoldportfolio.network.response.CurrencyRatesResponse
import com.kbratkovic.investmentgoldportfolio.network.response.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_PRICE_API_KEY
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
    suspend fun getCurrencyRatesBaseEUR(
        @Query("base") baseCurrency: String,
        @Query("apiKey") apiKey: String
    ): Response<CurrencyRatesResponse>


    @GET("https://anyapi.io/api/v1/exchange/rates/")
    suspend fun getCurrencyRatesBaseUSD(
        @Query("base") baseCurrency: String,
        @Query("apiKey") apiKey: String
    ): Response<CurrencyRatesResponse>

}