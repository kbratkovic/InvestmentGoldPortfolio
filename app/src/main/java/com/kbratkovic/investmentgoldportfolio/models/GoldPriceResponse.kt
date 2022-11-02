package com.kbratkovic.investmentgoldportfolio.models

data class GoldPriceResponse(
    val ask: Double,
    val bid: Double,
    val ch: Double,
    val chp: Double,
    val currency: String,
    val exchange: String,
    val high_price: Double,
    val low_price: Double,
    val metal: String,
    val open_price: Double,
    val open_time: Int,
    val prev_close_price: Double,
    val price: Double,
    val price_gram_18k: Double,
    val price_gram_20k: Double,
    val price_gram_21k: Double,
    val price_gram_22k: Double,
    val price_gram_24k: Double,
    val symbol: String,
    val timestamp: Int
)