package com.kbratkovic.investmentgoldportfolio.api

import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_PRICE_BASE_URL
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.RATES_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {


    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(GOLD_PRICE_BASE_URL)
//                .baseUrl(RATES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
    }
}