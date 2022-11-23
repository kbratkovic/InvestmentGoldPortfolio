package com.kbratkovic.investmentgoldportfolio.domain.mappers

import com.kbratkovic.investmentgoldportfolio.domain.models.CurrencyRates
import com.kbratkovic.investmentgoldportfolio.network.response.CurrencyExchangeRatesResponse

object CurrencyExchangeRatesMapper {

    fun buildFrom(response: CurrencyExchangeRatesResponse): CurrencyRates {
        return CurrencyRates(
            base = response.base,
            lastUpdate = response.lastUpdate,
            rates = CurrencyRates.Rates(
                EUR = response.rates.EUR,
                USD = response.rates.USD
            )
        )
    }

}