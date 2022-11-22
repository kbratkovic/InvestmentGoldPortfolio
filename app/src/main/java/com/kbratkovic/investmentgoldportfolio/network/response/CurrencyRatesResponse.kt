package com.kbratkovic.investmentgoldportfolio.network.response

data class CurrencyRatesResponse(
    val base: String = "",
    val lastUpdate: Int = 0,
    val rates: Rates = Rates()
) {
    data class Rates(
        val EUR: Double = 0.0,
        val USD: Double = 0.0,
//    val AUD: Double,
//    val BGN: Double,
//    val BRL: Double,
//    val CAD: Double,
//    val CHF: Double,
//    val CNY: Double,
//    val CZK: Double,
//    val DKK: Double,
//    val GBP: Double,
//    val HKD: Double,
//    val HRK: Double,
//    val HUF: Double,
//    val IDR: Double,
//    val ILS: Double,
//    val INR: Double,
//    val ISK: Double,
//    val JPY: Double,
//    val KRW: Double,
//    val MXN: Double,
//    val MYR: Double,
//    val NOK: Double,
//    val NZD: Double,
//    val PHP: Double,
//    val PLN: Double,
//    val RON: Double,
//    val SEK: Double,
//    val SGD: Double,
//    val THB: Double,
//    val TRY: Double,
//    val ZAR: Double
    )
}