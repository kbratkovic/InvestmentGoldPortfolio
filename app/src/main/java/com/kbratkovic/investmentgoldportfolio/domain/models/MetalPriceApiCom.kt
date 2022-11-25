package com.kbratkovic.investmentgoldportfolio.domain.models

data class MetalPriceApiCom(
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
