package com.kbratkovic.investmentgoldportfolio.domain.mappers

import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPrice
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceResponse


object MetalPriceMapper {

    fun buildFrom(response: MetalPriceResponse): MetalPrice {
        return MetalPrice(
            base = response.base,
            rates = MetalPrice.Rates(
                EUR = response.rates.EUR,
                XAU = response.rates.XAU,
                XAG = response.rates.XAG,
                XPT = response.rates.XPT,
                XPD = response.rates.XPD
            ),
            success = response.success,
            timestamp = response.timestamp
        )
    }
}
