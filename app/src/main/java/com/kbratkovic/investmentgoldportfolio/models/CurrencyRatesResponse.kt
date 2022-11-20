package com.kbratkovic.investmentgoldportfolio.models

data class CurrencyRatesResponse(
    val base: String,
    val lastUpdate: Int,
    val rates: Rates
)