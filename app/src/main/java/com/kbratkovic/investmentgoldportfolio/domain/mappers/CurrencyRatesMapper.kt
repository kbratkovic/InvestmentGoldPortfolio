package com.kbratkovic.investmentgoldportfolio.domain.mappers

import com.kbratkovic.investmentgoldportfolio.domain.models.CurrencyRates
import com.kbratkovic.investmentgoldportfolio.network.response.CurrencyRatesResponse

object CurrencyRatesMapper {

    fun buildFrom(currencyRatesResponse: CurrencyRatesResponse): CurrencyRates {
        return CurrencyRates(
            base = currencyRatesResponse.base,
            lastUpdate = currencyRatesResponse.lastUpdate,
            rates = CurrencyRates.Rates(
                EUR = currencyRatesResponse.rates.EUR,
                USD = currencyRatesResponse.rates.USD
            )
        )
    }

}