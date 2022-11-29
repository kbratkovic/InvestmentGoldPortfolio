package com.kbratkovic.investmentgoldportfolio.network

import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceApiComResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("https://api.metalpriceapi.com/v1/latest/")
    suspend fun getCurrentGoldPriceFromMetalPriceApiCom(
        @Query("api_key") api_key: String,
        @Query("base") base: String,
        @Query("currencies") currencies: String
    ): Response<MetalPriceApiComResponse>

}