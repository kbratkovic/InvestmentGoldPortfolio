package com.kbratkovic.investmentgoldportfolio.network

import com.kbratkovic.investmentgoldportfolio.network.response.CurrencyExchangeRatesResponse
import com.kbratkovic.investmentgoldportfolio.network.response.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceApiComResponse
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_PRICE_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    // GoldAPI.io
//    @Headers("x-access-token: $GOLD_PRICE_API_KEY")
//    @GET("{symbol}/{currency}")
//    suspend fun getCurrentGoldPrice(
//        @Path("symbol") symbol: String = "XAU",
//        @Path("currency") currency: String = "EUR"
//    ): Response<GoldPriceResponse>


    // metalpriceapi.com
    @GET("https://api.metalpriceapi.com/v1/latest/")
    suspend fun getCurrentGoldPriceFromMetalPriceApiCom(
        @Query("api_key") api_key: String,
        @Query("base") base: String,
        @Query("currencies") currencies: String
    ): Response<MetalPriceApiComResponse>



    // anyapi.io
    @GET("https://anyapi.io/api/v1/exchange/rates/")
    suspend fun getCurrencyRatesBaseEUR(
        @Query("base") baseCurrency: String,
        @Query("apiKey") apiKey: String
    ): Response<CurrencyExchangeRatesResponse>


    @GET("https://anyapi.io/api/v1/exchange/rates/")
    suspend fun getCurrencyRatesBaseUSD(
        @Query("base") baseCurrency: String,
        @Query("apiKey") apiKey: String
    ): Response<CurrencyExchangeRatesResponse>

}