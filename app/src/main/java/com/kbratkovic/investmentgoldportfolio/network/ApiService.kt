package com.kbratkovic.investmentgoldportfolio.network

import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("latest/")
    suspend fun getMetalPriceFromApi(
        @Query("api_key") api_key: String,
        @Query("base") base: String,
        @Query("currencies") currencies: String
    ): Response<MetalPriceResponse>

}