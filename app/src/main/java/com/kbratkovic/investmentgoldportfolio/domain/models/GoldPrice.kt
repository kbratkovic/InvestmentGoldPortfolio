package com.kbratkovic.investmentgoldportfolio.domain.models

data class GoldPrice(
    val currency: String = "",
    val high_price: Double = 0.0,
    val low_price: Double = 0.0,
    val metal: String = "",
    val price: Double = 0.0,
    val timestamp: Int = 0,
    val price_gram_24k: Double = 0.0,
    )
