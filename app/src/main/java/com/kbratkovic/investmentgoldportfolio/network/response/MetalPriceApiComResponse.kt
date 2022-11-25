package com.kbratkovic.investmentgoldportfolio.network.response

data class MetalPriceApiComResponse(
    val base: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
) {
    data class Rates(
        val EUR: Double,
        val XAU: Double
    )
}