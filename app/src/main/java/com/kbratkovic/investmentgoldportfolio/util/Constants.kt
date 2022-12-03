package com.kbratkovic.investmentgoldportfolio.util

import com.kbratkovic.investmentgoldportfolio.BuildConfig

class Constants {

    companion object {
        const val API_KEY = BuildConfig.apiKey
        const val BASE_API_URL = "https://api.metalpriceapi.com/v1/"

        const val GOLD_CODE = "XAU"
        const val SILVER_CODE = "XAG"
        const val PLATINUM_CODE = "XPT"
        const val PALLADIUM_CODE = "XPD"
        const val CURRENCY_USD_CODE = "USD"
        const val CURRENCY_EUR_CODE = "EUR"
        const val WEIGHT_GRAM_CODE = "gram"
        const val WEIGHT_TROY_OUNCE_CODE = "troy ounce"
        const val WEIGHT_GRAM_SHORT_CODE = "g"
        const val WEIGHT_TROY_OUNCE_SHORT_CODE = "oz t"

        const val CONVERT_TROY_OUNCE_CODE = 31.1035
    }
}