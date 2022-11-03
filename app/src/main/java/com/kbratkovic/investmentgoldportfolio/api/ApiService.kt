package com.kbratkovic.investmentgoldportfolio.api

import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path


interface ApiService {

    @Headers("x-access-token: $API_KEY")
    @GET("{symbol}/{currency}")
    suspend fun getCurrentGoldPrice(
        @Path("symbol") symbol: String = "XAU",
        @Path("currency") currency: String = "EUR"
    ): Response<GoldPriceResponse>

}