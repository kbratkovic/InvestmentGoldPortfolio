package com.kbratkovic.investmentgoldportfolio.network.response

data class MetalPriceResponse(
    val base: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
) {
    data class Rates(
        val EUR: Double,
        val XAU: Double,
        val XAG: Double,
        val XPT: Double,
        val XPD: Double
    )
}