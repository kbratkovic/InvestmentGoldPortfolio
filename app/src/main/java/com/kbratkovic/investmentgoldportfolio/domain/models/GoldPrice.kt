package com.kbratkovic.investmentgoldportfolio.domain.models

data class GoldPrice(
    val currency: String,
    val high_price: Double,
    val low_price: Double,
    val metal: String,
    val price: Double,
    val timestamp: Int
)
