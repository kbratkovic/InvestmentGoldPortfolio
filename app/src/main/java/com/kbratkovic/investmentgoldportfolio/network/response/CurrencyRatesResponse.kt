package com.kbratkovic.investmentgoldportfolio.network.response

data class CurrencyRatesResponse(
    val base: String,
    val lastUpdate: Int,
    val rates: Rates
)