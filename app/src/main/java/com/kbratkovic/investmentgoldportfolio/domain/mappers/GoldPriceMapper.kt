package com.kbratkovic.investmentgoldportfolio.domain.mappers

import com.kbratkovic.investmentgoldportfolio.domain.models.GoldPrice
import com.kbratkovic.investmentgoldportfolio.network.response.GoldPriceResponse

object GoldPriceMapper {

    fun buildFrom(goldPriceResponse: GoldPriceResponse): GoldPrice {
        return GoldPrice(
            currency = goldPriceResponse.currency,
            high_price = goldPriceResponse.high_price,
            low_price = goldPriceResponse.low_price,
            metal = goldPriceResponse.metal,
            price = goldPriceResponse.price,
            timestamp = goldPriceResponse.timestamp
        )
    }

}