package com.kbratkovic.investmentgoldportfolio.domain.mappers

import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPriceApiCom
import com.kbratkovic.investmentgoldportfolio.network.response.MetalPriceApiComResponse


object MetalPriceApiComMapper {

    fun buildFrom(response: MetalPriceApiComResponse): MetalPriceApiCom {
        return MetalPriceApiCom(
            base = response.base,
            rates = MetalPriceApiCom.Rates(
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
